Download the template and upload into NiFi instance

Before running the template, it is important to create files in a directory `/home/nobleprog/Documents`

```shell
# change directory
$ > cd /home/nobleprog/Documents/

# create directories if does not exist
$ > mkdir -p first-example/get-file first-example/put-file

# change directory to get-file
$ > cd first-example/get-file

# create file
$ > echo "Hello, world! from NiFi" > data.txt
```

### Configuring Processors

***GetFile***

| Name | Value |
| ---- | ----- |
| Input Directory | `/home/nobleprog/Documents/first-example/get-file` |

***PutFile***

| Name | Value |
| ---- | ----- |
| Input Directory | `/home/nobleprog/Documents/first-example/put-file` |

