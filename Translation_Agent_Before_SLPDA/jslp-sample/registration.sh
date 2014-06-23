#!/bin/bash

java -Dorg.apache.commons.logging.log=org.apache.commons.logging.impl.SimpleLog  -Dorg.apache.commons.logging.simplelog.defaultlog=trace -Dnet.slp.traceMsg=true -Dnet.slp.interfaces=fe80::222:19ff:fe19:184e -Dnet.slp.traceReg=true -cp jslp.jar:commons-logging-1.0.4.jar:bin Registration

