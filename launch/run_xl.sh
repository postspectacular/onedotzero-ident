#!/bin/sh
java -Xms256m -Xmx1512m -Djava.library.path=. -Djava.util.logging.config.file=config/logging.properties -jar odzgen.jar
