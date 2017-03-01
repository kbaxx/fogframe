#!/usr/bin/env bash
# reasoner/test/{countT1}/{countT2}/{countT3}/{countT4}/{minutes}
#http post http://192.168.1.101:8080/reasoner/test/3/2/3/0/2
http post http://192.168.1.101:8080/reasoner/test/1/1/1/1/2
#http post http://192.168.1.101:8080/reasoner/test/1/1/1/0/2

# no time limit
#http post http://192.168.1.101:8080/reasoner/test/3/2/3/0/-1

# 7 services - 3 minutes
#http post http://192.168.1.101:8080/reasoner/test/1/2/3/1/5

# 45 services - 5 minutes
#http post http://192.168.1.101:8080/reasoner/test/10/10/20/5/5

# 2 - services - no time limit (just to test the cloud db saving)
#http post http://192.168.1.101:8080/reasoner/test/1/0/0/1/-1



# EVALUATION SCENARIOS
# ----------------------------------------------------------------------------------------------------------------------

# 6 services - 5 minutes (VM stopped after migration)
# #1 all devices online but fc1 - then add it (2x t2 services migrated from cloud to fc1 and VM stopped)
# #2 all devices online - then remove fc1 (2x t2 services deployed in the cloud)
#http post http://192.168.1.101:8080/reasoner/test/1/2/3/0/5

# 24 services - 5 minutes (VM stopped after migration)
# #1 all devices online but fc1 - then add it (2x t2 services migrated from cloud to fc1 and VM stopped)
# #2 all devices online - then remove fc1 (2x t2 services deployed in the cloud)
#http post http://192.168.1.101:8080/reasoner/test/4/8/12/0/5

# 25 services - 5 minutes
#http post http://192.168.1.101:8080/reasoner/test/4/8/12/1/5

# 26 services - 5 minutes
#http post http://192.168.1.101:8080/reasoner/test/4/8/12/2/10

# 50 services - 5 minutes (VM stopped after migration)
# #1 all devices online but fc1 - then add it (2x t2 services migrated from cloud to fc1)
# #2 all devices online - then remove fc1 (2x t2 services deployed in the cloud)
#http post http://192.168.1.101:8080/reasoner/test/10/8/22/10/5

# 7 services - 5 minutes (VM stays after migration as 1 container can only be in the cloud (t4))
# #1 all devices online but fc1 - then add it (2x t2 services migrated from cloud to fc1)
# #2 all devices online - then remove fc1 (2x t2 services deployed in the cloud)
#http post http://192.168.1.101:8080/reasoner/test/1/2/3/1/5


