#!/bin/bash

java -Dorg.apache.commons.logging.log=org.apache.commons.logging.impl.SimpleLog  -Dorg.apache.commons.logging.simplelog.defaultlog=trace -Dnet.slp.traceMsg=true -Dnet.slp.interfaces=fd00::222:6bff:fea9:5df0 -Dnet.slp.traceReg=true -cp jslp_test.jar:commons-logging-1.0.4.jar:bin Lookup
