![logo](https://github.com/keyban/fogframe/blob/master/logo_fogframe.png)
# fogframe

This repository embodies a set of microservices developed in Java together forming a researched fog computing framework. The project consists of the cloud-fog middleware, fog control node, fog cell, hostmonitor, and the fog data.

* <b>Cloud-fog middleware (CFM):</b> Cloud middleware that handles the service deployment in the cloud and the data propagation to cloud services.
* <b>Fog control node (FCN):</b> Control node that orchestrates subjacent fog colonies consisting of other fog control nodes and fog cells. 
* <b>Fog cell (FC):</b> Node connected to fog control nodes and IoT devices to execute services and propagate data to a parent fog control node.
* <b>Fog data:</b> Models and Util classes shared amongst the other components.
* <b>Hostmonitor:</b> Monitoring applicatoin that monitors the host and sends the data to a corresponding Redis database for further processing.

## Software Specs
Tested with the following software versions:
* Docker 1.12.2
* Java 8
* Apache Maven 3.2.3
* OpenStack Cloud (Keystone v2.0, Nova)
* Hypriot OS (preinstalled on the Raspberry Pis)

## Hardware Specs
Tested with the following hardware:
* Macbook Pro 13", Early 2011 (8GB RAM, Intel Core i5)
* 1x Linksys Access Point
* 6x Raspberry Pis
* 2x Groove Pis with humidity and temperature sensors

## Install
To get the framework up and running, all Raspberrys need to be connected to the same WiFi and the different projects need to be built using `mvn install`. After that, the resulting JAR files and the corresponding run scripts in the folder `run_scripts` can be transferred to the Raspberry Pi. If both required JAR files (component and hostmonitor), the Dockerfile, and the run.sh script are transferred successfully, the components can be started.

In case of cloud usage, the cloud-fog middleware needs to be set up on a computer in the same network as the other fog devices. In order to get CFM running, a Redis database needs to be started using the `startDB.sh` script. After the db is online, the project can be started. 


## Usage
To start the different components the `run.sh` script needs to be executed.
```
$ sh run.sh
```

When all devices are paired successfully, the task requests can be sent to the fog control nodes. The required API endpoints can be checked in the code or in the online version of the thesis.

## License

Apache 2.0 © [Kevin Bachmann](kevin.bachmann@gmx.at)


## Sources
Cloud graphic by <a href="http://www.flaticon.com/authors/yannick">Yannick</a> from <a href="http://www.flaticon.com/">Flaticon</a> is licensed under <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0">CC BY 3.0</a>. Made with <a href="http://logomakr.com" title="Logo Maker">Logo Maker</a>
