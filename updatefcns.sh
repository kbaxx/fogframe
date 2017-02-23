#!/usr/bin/env bash
# fog control nodes
# --------------------------------------------------------------------------------------------------------------------
scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-1:~/bin
#scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-1:~/bin

scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-2:~/bin
#scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-2:~/bin

scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-3:~/bin
#scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-3:~/bin