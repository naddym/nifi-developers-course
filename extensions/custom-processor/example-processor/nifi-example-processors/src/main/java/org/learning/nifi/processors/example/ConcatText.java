/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.learning.nifi.processors.example;

import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.lifecycle.*;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.annotation.behavior.ReadsAttribute;
import org.apache.nifi.annotation.behavior.ReadsAttributes;
import org.apache.nifi.annotation.behavior.WritesAttribute;
import org.apache.nifi.annotation.behavior.WritesAttributes;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.SeeAlso;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.util.StandardValidators;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tags({"concat", "text", "append"})
@CapabilityDescription("Concatenates incoming flowfile content with the provided `concatenation value` property")
@SeeAlso({})
@ReadsAttributes({
        @ReadsAttribute(attribute="concat.delimiter", description="Delimiter to be used while concatenating incoming " +
                "flowfile with concatenation value property.")
})
@WritesAttributes({
        @WritesAttribute(attribute="concatenated.string.length", description="Length of concatenated string " +
                "written as attribute to the outgoing flowfile")
}
)
public class ConcatText extends AbstractProcessor {

    public static final String CONCAT_DELIMITER = "concat.delimiter";
    public static final String CONCATENATED_STRING_LENGTH = "concatenated.string.length";

    public static final PropertyDescriptor CONCAT_PROPERTY = new PropertyDescriptor
            .Builder().name("CONCAT_PROPERTY")
            .displayName("Concatenation Value")
            .description("Concatenates provided value to the incoming flowfile")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final Relationship SUCCESS = new Relationship.Builder()
            .name("Success")
            .description("Successfully processed flowfiles are transferred to this relationship")
            .build();

    private List<PropertyDescriptor> descriptors;

    private Set<Relationship> relationships;

    @Override
    protected void init(final ProcessorInitializationContext context) {
        descriptors = new ArrayList<>();
        descriptors.add(CONCAT_PROPERTY);
        descriptors = Collections.unmodifiableList(descriptors);

        relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships = Collections.unmodifiableSet(relationships);
    }

    @Override
    public Set<Relationship> getRelationships() {
        return this.relationships;
    }

    @Override
    public final List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return descriptors;
    }

    @OnAdded
    public void onAdded() {
        System.out.println("On Added is called");
    }

    @OnScheduled
    public void onScheduled(final ProcessContext context) {
        System.out.println("On Scheduled is called");
    }

    @OnUnscheduled
    public void onUnscheduled() {
        System.out.println("On Unscheduled is called");
    }

    @OnStopped
    public void onStopped() {
        System.out.println("On Stopped is called");
    }

    @OnRemoved
    public void onRemoved() {
        System.out.println("On Removed is called");
    }

    /**
     * NiFi's Framework calls this method whenever there is work exist
     * for the processor such as flowfile in incoming connection or if
     * processor is a source (with no incoming connection but wanted to be
     * triggered with this method, only when @TriggerWhenEmpty annotation
     * is added)
     * @param context
     * @param session
     */
    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) {

        // getting flowfile from the input queue
        // if the flowfile is not available, simply return
        // because, you don't want to apply any business logic
        // because you need a flowfile
        FlowFile flowFile = session.get();
        if ( flowFile == null ) {
            return;
        }

        // TODO implement
        // This TODO says implement your business logic here.

        // business logic

        // 1. reading the flowfile content
        // 2. concatenating the `From NiFi Course` to the incoming flowfile content
        // 3. transferring flowfile to next processor

        // 1. reading the incoming flowfile content

        // getting CONCAT_PROPERTY (Concatenation Value) property value
        final String property = context.getProperty(CONCAT_PROPERTY).getValue();

        // reading flowfile attribute `concat.delimiter`
        String delimiter = flowFile.getAttribute(CONCAT_DELIMITER);
        delimiter = delimiter == null ? "" : delimiter;

        final StringBuilder sb = new StringBuilder();

        // reading flowfile and storing as content
        session.read(flowFile, in -> sb.append(IOUtils.toString(in, Charset.defaultCharset())));

        final String content = sb.toString();

        // performing concatenation on incoming content
        // incoming flowfile content + delimiter + property value
        final String resultantString = content.concat(delimiter).concat(property);

        // writing the resultant string back to the flowfile
        flowFile = session.write(flowFile, out -> out.write(resultantString.getBytes(Charset.defaultCharset())));

        // add attribute `concatenation.string.length` to the flowfile
        flowFile = session.putAttribute(flowFile, CONCATENATED_STRING_LENGTH, String.valueOf(resultantString.length()));

        // transferring flowfile to next processor (success relationship)
        session.transfer(flowFile, SUCCESS);
    }
}
