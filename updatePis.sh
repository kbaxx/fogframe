#!/usr/bin/env bash
# fogcells
# --------------------------------------------------------------------------------------------------------------------
scp ./fogcell/target/fogcell-0.0.1-SNAPSHOT.jar pirate@fog-cell-1:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-cell-1:~/bin

scp ./fogcell/target/fogcell-0.0.1-SNAPSHOT.jar pirate@fog-cell-2:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-cell-2:~/bin

scp ./fogcell/target/fogcell-0.0.1-SNAPSHOT.jar pirate@fog-cell-3:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-cell-3:~/bin


# fog control nodes
# --------------------------------------------------------------------------------------------------------------------
scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-1:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-1:~/bin

scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-2:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-2:~/bin

scp ./fogcontrolnode/target/fogcontrolnode-0.0.1-SNAPSHOT.jar pirate@fog-control-node-3:~/bin
scp ./hostmonitor/target/hostmonitor-0.0.1-SNAPSHOT.jar pirate@fog-control-node-3:~/bin