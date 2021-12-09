* Originally based on https://thepracticaldeveloper.com/spring-boot-kafka-config/ 


* Default profile launches the server and the message producer microservice.
* Profile `consumer` launches a consumer microservice. Several can be executed in parallel.
* Server, producer and consumer should be split in different Maven artefacts, but for the moment we don't mind.