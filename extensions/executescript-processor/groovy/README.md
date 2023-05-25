## ExecuteScript
### Purpose
Executes a script given the flow file and a process session. The script is responsible for handling the incoming flow file (transfer to SUCCESS or remove, e.g.) as well as any flow files created by the script.

### ExecuteSQL Property Parameters
| Name | Description |
| ------ | ------ |
| Script Engine | The engine to execute scripts. E.g.: Groovy, Python, etc |
| Script File | Path to script file to execute. Only one of Script File or Script Body may be used |
| Script Body | Body of script to execute. Only one of Script File or Script Body may be used |
| Module Directory | Comma-separated list of paths to files and/or directories which contain modules required by the script. |

### Use Cases
1. Create functionalities that can not be performed by native NiFi processors

### Examples
   * Get flowfile and transfer to success relationship
   * Create a new flowfile
   * Read/Write attributes
   * Logging and Error Handling
   * Read/Write flowfile content
   * Adding dynamic properties
  
### Basics of Groovy Processors
This section covers the foundational building blocks for an using execute script processor.

To build custom logic, the execute script comes with variable bindings that are provided to the script to enable access to NiFi components.

1. **Session**: This is a reference to the ProcessSession assigned to the processor. The session allows you to perform operations on flow files such as create(), putAttribute(), and transfer(), as well as read() and write().

2. **Context**: This is a reference to the ProcessContext for the processor. It can be used to retrieve processor properties, relationships, Controller Services, and the StateManager.

3. **Log**: This is a reference to the ComponentLog for the processor. Use it to log messages to NiFi.

4. **Relationships**
* REL_SUCCESS:  This is a reference to the "success" relationship defined for the processor.
* REL_FAILURE: This is a reference to the "failure" relationship defined for the processor.

5. **Dynamic Properties**: Any dynamic properties defined in ExecuteScript are passed to the script engine as variables set to the PropertyValue object corresponding to the dynamic property. This allows you to get the String value of the property, but also to evaluate the property with respect to NiFi Expression Language, cast the value as an appropriate data type (such as Boolean, e.g.), etc.

Using the above bindings we will now perform simple tasks. To try out the below tasks, you can:
- (1) Drag and Drop an "Execute Script" processor, copy paste the code in the "Script Body" parameter, and use Groovy as the script engine.

<br>

### Task 1: Get flowfile and transfer to success relationship

In this example we will learn:
 - How to get flowfile from a connection queue
 - How to transfer it to downstream relationships

```java
// Code Snippet

// Use the get() method from the session object
flowFile = session.get()

// If the script requires a FlowFile to continue processing, 
// then it should immediately return if null is returned from session.get()
if(!flowFile) return

// After processing a flow file (new or incoming),
// you want to transfer the flow file to a relationship ("success" or "failure")

// Use the transfer(flowFile, relationship) method from the session object.
// The flowFile object is transferred to pre-defined success relationship - "REL_SUCCESS"
session.transfer(flowFile, REL_SUCCESS)
``` 

### Task 2: Create a new flowfile

In this example we will learn:
 - How to create a new empty flowfile and transfer to success relationship  

```java
// Code Snippet

// Use the create() method from the session object.
// This method returns a new FlowFile object, 
// which you can perform further processing on
flowFile = session.create()

// Transfer flowFile object to Success Relationship
session.transfer(flowFile, REL_SUCCESS)

```

### Task 3: Read/Write attributes

In this example we will learn:
  - How to read/write attributes from/to a flowfile
  - How to read/write multiple attributes from/to a flowfile

```java
// Code Snippet for reading/writing attributes

// Use the get() method from the session object 
// and check if flowfile is not empty 
flowFile = session.get()
if(!flowFile) return

// Use the getAttribute(attributeKey) method from the FlowFile object. 
// This method returns the String value for the given attributeKey,
// or null if the attributeKey is not found.
// Here, attribute filename is being fetched
myAttribute = flowFile.getAttribute('filename')

// Use the putAttribute(flowFile, attributeKey, attributeValue) method from the session object.
// Creates a new attribute called with key as "myAttributeKey"
// and value as variable value of "myAttribute"
flowFile = session.putAttribute(flowFile, 'myAttributeKey', myAttribute)


// Use the transfer(flowFile, relationship) method from the session object. 
session.transfer(flowFile, REL_SUCCESS)
```

```java
// Code Snippet for reading/writing multiple attributes

// Use the get() method from the session object 
// and check if flowfile is not empty 
flowFile = session.get()
if(!flowFile) return

// Use the getAttributes().each method from the FlowFile object. 
// This method returns a Map with String keys and String values
flowFile.getAttributes().each { key,value ->
  // Do something with the key/value pair
    }

// Use the putAllAttributes(flowFile, attributeMap) method from the session object.
// This is much more efficient than calling putAttribute() for each key/value pair
// Example: A map is created with 2 key-value pairs and written to flowFile
attrMap = ['myAttr1': '1', 'myAttr2': Integer.toString(2)]
flowFile = session.putAllAttributes(flowFile, attrMap)

// Use the transfer(flowFile, relationship) method from the session object. 
session.transfer(flowFile, REL_SUCCESS)
```

### Task 4: Logging and Error Handling

In this exmaple we will learn:
 - Logging messages at specified log levels
 - Error Handling using try catch block

*Note: In execute script processors error handling is highly recommended. In cases where errors are not being handled, the flowfiles remain stuck in connection queue and accumulate over a period of time. Therefore, it is important to handle errors and transfer to Failure relationship with appropriate exception message.*  

```java
// Code Snippet for logging

flowFile = session.get()
if (!flowFile) return

// Use the log variable with the warn(), trace(), debug(), info(), or error() methods

// Example for a simple logging using info method:
log.info('This message is now being logged')

// Example for a logging dynamic objects using {}:
// This message will be logged as "Found these things: Hello 1 true"
log.info('Found these things: {} {} {}', ['Hello',1,true] as Object[])

// Use the transfer(flowFile, relationship) method from the session object. 
session.transfer(flowFile, REL_SUCCESS)
```

```java
// Code Snippet for Error Handling

flowFile = session.get()
if (!flowFile) return

// Use try catch block
try {
    
    // Write your code here...

    session.transfer(flowFile, REL_SUCCESS)
} 

catch(e) {
    
    // Use catch block to log error message 
    log.error('Something went wrong', e)
    // Transfer flowFile object to Failure Relationship
    session.transfer(flowFile, REL_FAILURE)
}

```

### Task 5: Read/Write flowfile content

In this exmaple we will learn:
 - Reading flowFile Content
 - Writing flowFile Content

The content of a flow file is simply a collection of bytes and has no inherent structure, schema, format, etc. Input and Output (I/O) for the contents of flow files is provided via the ProcessSession API and thus they use the "session" variable for ExecuteScript. 

One way to do this is to pass a callback object into a call to session.read() or session.write(). An InputStream and/or OutputStream will be created for the FlowFile object, and the callback object will be invoked using the corresponding callback interface, with the InputStream and/or OutputStream references passed in for use by the callback.


```java
// Code Snippet for reading flowfile content

// Importing required classes
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

flowFile = session.get()
if(!flowFile)return

try{

// Declaring text as an empty string variable
def text = ''

// Cast a closure with an inputStream parameter to InputStreamCallback
session.read(flowFile, {inputStream ->
  text = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
} as InputStreamCallback)

// Write your code here...

// Transfer to success relationship
session.transfer(flowFile, REL_SUCCESS)
}

catch(e) {
    // Log error
    log.error('Something went wrong', e)
    // Transfer to failure relationship
    session.transfer(flowFile, REL_FAILURE)
}
```

```java
// Code Snippet for Writing to flowfile content

// Importing required classes
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

flowFile = session.get()
if(!flowFile) return

try{

// Declaring text as string variable
def text = 'Hello world!'

// Cast a closure with an outputStream parameter to OutputStreamCallback
flowFile = session.write(flowFile, {outputStream ->
  outputStream.write(text.getBytes(StandardCharsets.UTF_8))
} as OutputStreamCallback)

session.transfer(flowFile, REL_SUCCESS)
}

catch(e) {
    // Log error
    log.error('Something went wrong', e.toString())
    // Transfer to failure relationship
    session.transfer(flowFile, REL_FAILURE)
}
```

### Task 6: Adding dynamic properties

In this exmaple we will learn:
 - How to infer dyanmic properties

Dynamic properties, also called User-Defined properties, are properties for a processor for which a user can set both the property name and value. Not all processors support/use dynamic properties, but ExecuteScript will pass dynamic properties as variables which reference a PropertyValue object corresponding to the property's value. There are two important things to note here:

1. Because the property name is bound as-is to a variable name, the naming convention for dynamic properties must be supported for the specified programming language. For example, Groovy does not support period (.) as a valid variable character, so a dynamic property such as "my.value" will cause the processor to fail. A valid alternative in this case is "myValue".

2. The PropertyValue object is used (rather than a String representation of the value) to allow the script to perform various operations on the property's value before evaluating it to a String. If the property is known to contain a literal value, you can call the getValue() method on the variable to get its String representation. If instead the value could contain Expression Language or you want to cast the value to something other than String (such as the value 'true' to a Boolean object), there are methods for these operations too.

```java
// Code Snippet

// Importing required classes
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets

flowFile = session.get()
if(!flowFile) return

try{

// There are 2 user-defined dynamic properties added - "key1" and "key2"

// As the value for propperty "key1" is a literal string, value method returns 
// string representation of the value of the dynamic property.
def var1 = key1.value

// As the value for property "key2" uses Nifi Expression Language, 
// evaluateAttributeExpressions(flowFile) method is used in conjuction with value method
def var2 = key2.evaluateAttributeExpressions(flowFile).value

// Creating a text variable using both variables 
text = var1 + " " + var2 

// Cast a closure with an outputStream parameter to OutputStreamCallback
flowFile = session.write(flowFile, {outputStream ->
  // Write this text to flowfile
  outputStream.write(text.getBytes(StandardCharsets.UTF_8))
} as OutputStreamCallback)
session.transfer(flowFile, REL_SUCCESS)
}
catch(e) {
    // Log error
    log.error('Something went wrong', e.toString())
    // Transfer to failure relationship
    session.transfer(flowFile, REL_FAILURE)
}
```

Now that you are ready with the building blocks of execute script processor, the next section will help you build your custom logic. 
### Additional Notes
1. Refer to [Execute Script Cookbook - Part 1,2,3](https://community.cloudera.com/t5/Community-Articles/ExecuteScript-Cookbook-part-1/ta-p/248922) for detailed documentation
