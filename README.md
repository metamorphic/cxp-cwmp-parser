# Spring XD Module for TR-069 Data Ingestion

## Overview

This Spring XD module receives JSON String input from cxp-splunk-source and ingests CWMP XML data embedded within the JSON. It outputs POJOs which can then be received by another module such as hdfs-dataset.

<pre>
stream create --name tr69_ingest --definition "
splunk
    --host=<splunk-server>
    --username=<username>
    --password=<password>
    --owner=<owner>
    --searchQuery='index=tr069'
    --fixedDelay=30
    --fixedDelayUnit=SECONDS
    --initEarliestTime='-1d' |
cwmp-parser |
filter
    --expression=#root.headers.get('TR69IngestErrorCode')=='0' |
hdfs-dataset
    --inputType='application/x-java-object;type=cxp.ingest.model.InternetGatewayDevice'
    --format=parquet --fsUri='hdfs://<namenode-host>:8020'
    --basePath=<base-path>"
</pre>

## Operation Guide
### Pre-requisites

* This module receives input from the splunk source module (source:splunk) Splunk source module must be installed.

* This module sends output to hdfs-dataset module in order to write parquet files. HDFS must be installed and a location to write files on HDFS must be available.

* The sink module must be aware of the type of POJO. Therefore the POJO jar file must be installed in the class path of the hdfs-dataset sink.

### Module Build and Setup

To build the module jar file from the sourcecode

`./gradlew clean build`

To upload the module to Spring XD

`xd:>module upload --type processor --name cwmp-parser --file <path-to>/build/libs/cwmp-parser-1.0.jar`

To delete the module (or before uploading a newer version of jar file)

`xd:>module delete processor:cwmp-parser`

In order to get the POJO jar file, locate InternetGatewayDevice.class in build directory and run

`jar cvf InternetGatewayDevice.jar InternetGatewayDevice.class`

Place this jar file in the classpaths of hdfs-dataset and other sinks which needs to receive the InternetGatewayDevice object.

### Single-Stream Setup

(1) Create a stream for writing valid records into a parquet file, using hdfs-dataset sink. To filter out the error records from cwmp-parser, apply a filter after the cwmp-parser.

`xd:>stream create --name tr069_ingest --definition "splunk --host=<splunk-host> --username=<username> --password=<password> --owner=<owner> --searchQuery='<SearchQuery>' --fixedDelay=10 --fixedDelayUnit=SECONDS --initEarliestTime='<InitEarliestTime>'| cwmp-parser | filter --expression=#root.headers.get('TR69IngestErrorCode')=='0' |  hdfs-dataset --format=parquet --fsUri='hdfs://<namenode>:<port>' --basePath=<dir/path>"`

`xd:>stream deploy --name tr069_ingest`

Alternatively, use a manifest in the deploy command to only deploy on specific containers. This will be required if the POJO jar is only installed on specific containers. For example:

`xd:>stream deploy --name tr69_ingest --properties "module.*.criteria=groups.contains('openvpn-01')"`

(2) In order to write out parquet files, undeploy the stream. Destroy the stream if no longer required.

`xd:>stream undeploy --name tr069_ingest`

`xd:>stream destroy --name tr069_ingest`

### Multiple-Stream Setup

This version allows additional metrics and logging.

(1) Setup the first stream using the same approach as step 1 in Single-Stream setup, but do not deploy.

(2) CWMP-parser attaches an additional field to the header of the output message. The header field IngestErrorCode can be checked and filtered to separate good, warning and error records. Create another stream to tap from the first stream and filter only warning records, and write as a log file.

`xd:>stream create --name tr69_ingest_warnings --definition "tap:stream:tr69_ingest.cwmp-parser > filter --expression=#root.headers.get('TR69IngestErrorCode')=='1' | transform --expression=#root.headers.get('TR69IngestErrorMessage')+'|'+#root.headers.get('TR69IngestErrorDump')  | file --name=tr69_ingest_warnings.txt --dir=/tmp "`

(3) Create a third stream to tap from the first stream and filter only error records, and write as a log file.

`xd:>stream create --name tr69_ingest_errors --definition "tap:stream:tr69_ingest.cwmp-parser > filter --expression=#root.headers.get('TR69IngestErrorCode')=='2' | transform --expression=#root.headers.get('TR69IngestErrorMessage')+'|'+#root.headers.get('TR69IngestErrorDump')  | file --name=tr69_ingest_errors.txt --dir=/tmp "`

(4) In order to capture metrics, such as the number of good records, warning and error records, create three additional streams, each of which into will feed into a counter.

`xd:>stream create --name tr69_ingest_ok_counter --definition "tap:stream:tr69_ingest.filter > counter --name=tr69_ok_count"`

`xd:>stream create --name tr69_ingest_warning_counter --definition "tap:stream:tr69_ingest_warnings.filter > counter --name=tr69_warnings_count"`

`xd:>stream create --name tr69_ingest_error_counter --definition "tap:stream:tr69_ingest_errors.filter > counter --name=tr69_errors_count"`

(5) Deploy all streams. Deploy the counters first, and then error and warning streams. Deploy primary stream at the last step.

`xd:>stream deploy --name "tr69_ingest_ok_counter"`

`xd:>stream deploy --name "tr69_ingest_warning_counter"`

`xd:>stream deploy --name "tr69_errors_count"`

`xd:>stream deploy --name "tr69_ingest_warning"`

`xd:>stream deploy --name "tr69_ingest_errors"`

`xd:>stream deploy --name "tr69_ingest"`

(6) In order to write out parquet files, undeploy the primary stream.

`xd:>stream undeploy --name tr069_ingest`

### Accessing Output Files

Observe the parquet files in the hdfs location specified earlier, i.e.

`--fsUri='hdfs://<namenode>:<port>' --basePath=<dir/path>"`

If no --basePath was specified, xd creates directories as below.

`hdfs dfs -ls /xd/tr069_ingest/internetgatewaydevice`

If you have parquet-tools installed, you can view the schema and contents of the parquet file from the command line, e.g.

`hadoop jar parquet-tools.jar schema /xd/tr69_ingest/internetgatewaydevice/a759fe8c-c719-458a-b9e8-eb7a32415ade.parquet`

`hadoop jar parquet-tools.jar head /xd/tr69_ingest/internetgatewaydevice/a759fe8c-c719-458a-b9e8-eb7a32415ade.parquet`

### Accessing Log and Metrics

(1) To check the number of good/warning/error records processed by each stream

`xd:>counter display tr69_ok_count`

`xd:>counter display tr69_errors_count`

`xd:>counter display tr69_warnings_count`

(2) To reset the counters

`xd:>counter delete tr69_ok_count`

`xd:>counter delete tr69_errors_count`

`xd:>counter delete tr69_warnings_count`

(3) The error log file and the warning log file are in the directory specified when calling the file sink earlier.

`file --name=tr69_ingest_errors.txt --dir=/tmp`

(4) If standard error on the container is also indexed by Splunk, the log messages will be also accessible on Splunk.
