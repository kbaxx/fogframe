#!/usr/bin/env bash
# reasoner/test/{countT1}/{countT2}/{countT3}/{countT4}/{minutes}
date
http post http://192.168.1.101:8080/reasoner/test/2/0/8/0/5 --timeout=1

sleep 119
date

http post http://192.168.1.101:8080/reasoner/test/2/0/8/0/2 --timeout=1

sleep 119
date

http post http://192.168.1.101:8080/reasoner/test/2/0/8/0/5 --timeout=1

sleep 119
date

http post http://192.168.1.101:8080/reasoner/test/2/0/8/0/1 --timeout=1

sleep 119
date

http post http://192.168.1.101:8080/reasoner/test/2/0/8/0/1 --timeout=1
