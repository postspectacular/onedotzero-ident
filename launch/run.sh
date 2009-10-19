#!/bin/sh
cd "${0%/*}"
java -Xms256m -Xmx640m -Djava.library.path=bin -Djava.util.logging.config.file=config/logging.properties -Xdock:icon=assets/icon/odz128x128.icns -jar odzgen.jar
