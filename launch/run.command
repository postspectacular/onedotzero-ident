#!/bin/sh
java -Xms480m -Xmx512m -Djava.library.path=. -Djava.util.logging.config.file=config/logging.properties -jar odzinstall.jar

