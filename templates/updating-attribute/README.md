Download the template and upload into NiFi instance

### Configuring Processors and Controller Services


***GenerateFlowFile*** 

Dynamic Properties.

All dynamic properties will be come flowfile attributes

| Name | Value |
| ---- | ----- |
| `name` | `John` |


***UpdateAttribute***

Dynamic Properties

| Name | Value |
| ---- | ----- |
| `name` | `${name:toUpper()}` |


***LogAttribute***

No additional configuration is required

