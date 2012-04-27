import matplotlib.pyplot as plot
import aggrutils
import sys


jobids = [80, 54, 76, 55, 86, 87, 65, 78, 79, 88, 91, 64, 77, 84, 53]
#jobids = [82, 90, 71, 81, 67, 72, 83, 73, 74, 66, 75, 85, 89, 68, 69, 70]

timesmap = {}

for jobid in jobids:
	print ("about to load " + str(jobid)) 
	
	if jobid < 59:
		vassalid = 401
	else:
		vassalid = 571
	
	times, timestaken, numvassals = aggrutils.load_vassal_steal_stats(jobid, vassalid)
	avgtimetaken = 1.0 * sum(timestaken) / len(timestaken)
	
	if numvassals not in timesmap:
		timesmap[numvassals] = []
	timesmap[numvassals].append(avgtimetaken)

xs = []
ys = []
for k in sorted(timesmap.keys()):
	v = timesmap[k]
	avg = 1.0 * sum(v) / len(v)
	xs.append(k)
	ys.append(avg)

print(xs)
print(ys)

plot.plot(xs, ys, '-o')
plot.xlabel("Number of Machines")
plot.ylabel("Average Work Theft Request Time (ms)")
plot.show()
