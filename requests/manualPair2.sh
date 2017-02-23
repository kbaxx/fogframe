#!/usr/bin/env bash

# fcn's
http post http://192.168.1.101:8080/comm/manualPair/ ip=192.168.1.101 port=8082 type=CLOUD_FOG_MIDDLEWARE
http post http://192.168.1.106:8080/comm/manualPair/ ip=192.168.1.101 port=8080
http post http://192.168.1.107:8080/comm/manualPair/ ip=192.168.1.101 port=8080

#http post http://192.168.1.105:8080/comm/manualPair/ ip=192.168.1.101 port=8082 type=CLOUD_FOG_MIDDLEWARE
#http post http://192.168.1.106:8080/comm/manualPair/ ip=192.168.1.105 port=8080
#http post http://192.168.1.107:8080/comm/manualPair/ ip=192.168.1.105 port=8080

# fc's
http post http://192.168.1.110:8081/comm/manualPair/ ip=192.168.1.106 port=8080
http post http://192.168.1.111:8081/comm/manualPair/ ip=192.168.1.106 port=8080
http post http://192.168.1.112:8081/comm/manualPair/ ip=192.168.1.107 port=8080
