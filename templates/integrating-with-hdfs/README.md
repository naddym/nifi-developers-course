# Integration with Apache Hadoop

This example demonstrates NiFi Integration with Apache Hadoop.


### Stack overview

* NiFi
* Hadoop

## Prerequisites
* Install [Docker](https://www.docker.com/)
* Install [Docker Compose](https://docs.docker.com/compose/install/)

## Deploy ecosystem stack

Step 1: Clone the repository and checkout usecase

```shell
$ > git clone https://github.com/naddym/nifi-developers-course.git
$ > cd usecase
```

Step 2: Deploy stack with docker-compose

```shell
$ > sudo docker-compose up
```

## Pre-requesite changes to Containers

Following changes are needed before running the dataflow

### Hadoop

Get into Hadoop's namenode container and execute following commands

```shell
$ > sudo docker exec -it namenode bash

# listing existing files in hadoop
$ > hdfs dfs -ls /

# creating /user/nifi directory for storing airports data
$ > hdfs dfs -mkdir -p /user/nifi
```

### Template
Download `integrating-with-hdfs.xml` file and upload as template into NiFi instance and run the dataflow. If you want to follow along step by step with instructor then navigate to `Configuring Processors and Controller Services` section 

### Configuring Processors and Controller Services

***GenerateFlowFile*** 

Configuring `GenerateFlowFile` processor is simple. Just copy below data to `Custom Text` property of GenerateFlowFile Processor

```json
[
    {
        "id": 40814,
        "ident": "SK-377",
        "type": "small_airport",
        "name": "Barbosa Airport",
        "latitude_deg": 5.943333,
        "longitude_deg": -73.611389,
        "elevation_ft": 5176,
        "continent": "SA",
        "iso_country": "CO",
        "iso_region": "CO-SAN",
        "municipality": "Barbosa",
        "scheduled_service": "no",
        "gps_code": null,
        "iata_code": null,
        "local_code": "BSA",
        "home_link": null,
        "wikipedia_link": null,
        "keywords": null
    }
]
```

***UpdateAttribute***

Dynamic Properties

| Name | Value |
| ---- | ----- |
| `filename` | `data.json` |


***PutHDFS***

| Name | Value |
| ---- | ----- |
| Hadoop Configuration Resources | `/opt/nifi/nifi-current/conf/core-site.xml` |
| Directory | `/user/nifi` |