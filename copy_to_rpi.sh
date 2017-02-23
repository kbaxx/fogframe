#!/usr/bin/env bash
# fogcells
# --------------------------------------------------------------------------------------------------------------------
scp ./requests/simulateOverload.sh pirate@fog-cell-1:~/bin
scp ./requests/simulateOverload.sh pirate@fog-cell-2:~/bin
scp ./requests/simulateOverload.sh pirate@fog-cell-3:~/bin


# fog control nodes
# --------------------------------------------------------------------------------------------------------------------
scp ./requests/simulateOverload.sh pirate@fog-control-node-1:~/bin
scp ./requests/simulateOverload.sh pirate@fog-control-node-2:~/bin
scp ./requests/simulateOverload.sh pirate@fog-control-node-3:~/bin