#!/bin/sh

PORT=5000
mvn package 
java -cp target/classes:target/dependency/* ro.ieugen.bjug14.CamelStart
