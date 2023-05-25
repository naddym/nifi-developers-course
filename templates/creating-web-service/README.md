Download the template and upload into NiFi instance

### Configuring Processors and Controller Services

***HandleHTTPRequest***

| Name | Value |
| ---- | ----- |
| Listening Port | `8081` |
| HTTP Context Map | `Create a new StandardHttpContextMap Controller Service` |

***StandardHttpContextMap*** Controller Service

No additional configuration is required, just enable the controller service

***ReplaceText***

| Name | Value |
| ---- | ----- |
| Replacement Value | `Hello from NiFi Course` |
| Replacement Strategy | `Always Replace` |

***HandleHTTPResponse***

| Name | Value |
| ---- | ----- |
| HTTP Status Code | `200` |
| HTTP Context Map | `Use existing StandardHttpContextMap Controller Service created from HandleHTTPRequest Processor` |

