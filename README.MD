# File Indexer for Elgg (Pleio)

# Building with Maven
To build the project with Maven you can issue the following command:
mvn package

This will automatically download all the dependencies, compile the File
Indexer project and package it into a fat jar.

The output will be written to target/release

# Installation
1. Copy the jar file to the destination location. For example:
    mkdir /usr/local/FileIndexer
    cp FileIndexer.jar /usr/local/FileIndexer
2. Edit the config.yml file and configure the settings for RabbitMQ and ElasticSearch
3. Edit the service.sh helper script so the PATH_TO_JAR variable points
to the location to where you copied the FileIndexer.jar file.
4. Make the service.sh script executable:
    chmod +x service.sh
5. [Optional]: copy the service.sh script to a location in the PATH, i.e.:
    cp service.sh /usr/local/bin
6. Start the service by running:
    service.sh start
    
# Managing the service
After starting the service the service.sh script will write a pid file to /tmp.
You can use the service.sh script to stop the service:
    service.sh stop
    
or to restart the service:
    service.sh restart
    


