import matplotlib.pyplot as plot
import json
import sys
import aggrutils

NUM_START_NODES_MAP = {10:39, 20:39, 40:9, 50:46, 60:36, 70:26, 80:16}

jobid = 59
if len(sys.argv) > 1:
	jobid = sys.argv[1]

vassalid = 571
if len(sys.argv) > 2:
	jobid = int(sys.argv[2])

lordfilename = "/home/sryza/logs/lord" + str(jobid) + ".log"

lordstats = json.load(open(lordfilename, 'r'))
lordstealtimestaken = lordstats["stealsTimeTaken"]
lordstealtimes = lordstats["stealTimes"]
starttime = lordstats["finish time"] - lordstats["totalTime"]

vassalstatses = aggrutils.load_stats(jobid, vassalid)

vassalstealtimestaken = []
vassalstealtimes = []

numvassals = len(vassalstatses)
for vassalstats in vassalstatses:
	stealtimes = vassalstats["workStolenTimes"]
	timestaken = vassalstats["workStealLats"]
	stealtimes, timestaken = aggrutils.sort_by_list1(stealtimes, timestaken)
	vassalstealtimestaken.extend(timestaken[0:len(timestaken)-1])
	vassalstealtimes.extend(stealtimes[0:len(stealtimes)-1])

print(len(lordstealtimes))
print(len(vassalstealtimes))
print(len(lordstealtimestaken))
print(len(vassalstealtimestaken))

lordstealtimes, lordstealtimestaken = aggrutils.sort_by_list1(lordstealtimes, lordstealtimestaken)
vassalstealtimes, vassalstealtimestaken = aggrutils.sort_by_list1(vassalstealtimes, vassalstealtimestaken)
#aggrutils.normalize_times([lordstealtimes, vassalstealtimes])
for i in range(len(lordstealtimes)):
	lordstealtimes[i] -= starttime
for i in range(len(vassalstealtimes)):
	vassalstealtimes[i] -= starttime

vassalstealtimes = vassalstealtimes[NUM_START_NODES_MAP[numvassals]:]
vassalstealtimestaken = vassalstealtimestaken[NUM_START_NODES_MAP[numvassals]:]

print(NUM_START_NODES_MAP[numvassals])

plot.plot(vassalstealtimes, vassalstealtimestaken, 'bo')
plot.plot(lordstealtimes, lordstealtimestaken, 'ro')
plot.xlabel("Time (ms)")
plot.ylabel("Request Time (ms)")
plot.show()

