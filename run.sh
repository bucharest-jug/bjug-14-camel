#!/bin/sh

mvn package 
java -cp target/classes:target/dependency/* ro.ieugen.bjug14.CamelStart
