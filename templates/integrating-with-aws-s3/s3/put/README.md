Download the template and upload into NiFi instance

### Configuring Processors

***GenerateFlowFile*** 

Configuring `GenerateFlowFile` processor is simple. Just copy below data to `Custom Text` property of GenerateFlowFile Processor

```json
{
  "id" : 6523,
  "ident" : "00A",
  "type" : "heliport",
  "name" : "Total Rf Heliport",
  "latitude_deg" : 40.0708,
  "longitude_deg" : -74.93360137939453,
  "elevation_ft" : 11,
  "continent" : "NA",
  "iso_country" : "US",
  "iso_region" : "US-PA",
  "municipality" : "Bensalem",
  "scheduled_service" : "yes",
  "gps_code" : "00A",
  "iata_code" : null,
  "local_code" : "00A",
  "home_link" : null,
  "wikipedia_link" : null,
  "keywords" : null
}
```

***PutS3Object***

| Name | Value |
| ---- | ----- |
| Object Key | `data.json` |
| Bucket | `nifi-test--bucket` |
| Region | `US East (N. Virginia)` |
| Access Key ID | `provide the access key id` |
| Secret Access Key | `provide the secret access key` |