import matplotlib.pyplot as plot
import json
import sys

jobid = 59
if len(sys.argv) > 1:
	jobid = sys.argv[1]

lordfilename = "/home/sryza/logs/lord" + str(jobid) + ".log"

stats = json.load(open(lordfilename, 'r'))
stealtimes = stats["stealTimes"]
stealtimes.sort()

starttime = stats["finish time"] - stats["totalTime"]

#subtract first steal time from all the other ones
for i in range(len(stealtimes)):
	stealtimes[i] -= starttime

counts = [x+1 for x in range(len(stealtimes))]

#print(stealtimes)
#print(counts)

plot.plot(stealtimes, counts, '-o')
plot.xlabel("Time (ms)")
plot.ylabel("Cumulative Number of Work Thefts")
plot.show()