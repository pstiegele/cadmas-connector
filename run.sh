#!/bin/bash
git pull
ant
java -jar "dist/Ardupilot Connector.jar"
