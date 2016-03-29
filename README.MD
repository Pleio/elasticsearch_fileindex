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
2. Rename config.yml.dist to config.yml and edit the file. The file contains
comments with information about each config setting.
3. Rename log4j2.xml.dist to log4j2.xml and edit the settings. Of particular
interest are the settings fileName and filePattern, which are attributes
of the RollingFile tag. For example if you prefer the file to be in its
own logs directory you can specify fileName="logs/indexer.log".

The log will automatically roll over when it reaches a certain size.
In our default configuration this size is 100 MB, you can adjust this value
in the SizeBasedTriggeringPolicy tag: <SizeBasedTriggeringPolicy size="100 MB" />

With the default configuration settings a maximum of 3 rolled over log
files will be kept. You can change this by changing the DefaultRolloverStrategy
max value: <DefaultRolloverStrategy max="3"/>

To increase or decrease the logging verbosity you can change the value of 
level in the Root tag: <Root level="info">.
Possible values are: off, fatal, error, warn, info, debug, trace, all

3. Edit the fileindexer.sh helper script so the PATH_TO_JAR variable points
to the location to where you copied the FileIndexer.jar file. Also change 
the PATH_TO_CONFIG variable if necessary. This should point to the path
containing your config.yml file.
4. Make the fileindexer.sh script executable:
    chmod +x fileindexer.sh
5. [Optional]: copy the fileindexer.sh script to a location in the PATH, i.e.:
    cp fileindexer.sh /usr/local/bin
6. Start the service by running:
    fileindexer.sh start
7. To see if the service is indeed running check the log file or confirm
by issuing the following command:
ps aux |grep $(cat /tmp/FileIndexer-pid)

# Managing the service
After starting the service the service.sh script will write a pid file to /tmp.
You can use the service.sh script to stop the service:
    service.sh stop
    
or to restart the service:
    service.sh restart
    


