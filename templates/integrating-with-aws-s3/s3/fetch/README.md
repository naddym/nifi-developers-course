Download the template and upload into NiFi instance

### Configuring Processors

***GenerateFlowFile*** 

We don't need any configuration

```

***FetchS3Object***

| Name | Value |
| ---- | ----- |
| Bucket | `nifi-test--bucket` |
| Object Key | `data.json` |
| Region | `US East (N. Virginia)` |
| Access Key ID | `provide the access key id` |
| Secret Access Key | `provide the secret access key` |