import matplotlib.pyplot as plot
import json
import sys

jobid = 59
if len(sys.argv) > 1:
	jobid = sys.argv[1]

lordfilename = "/home/sryza/logs/lord" + str(jobid) + ".log"

stats = json.load(open(lordfilename, 'r'))
numFailedAttempts = stats["numFailedAttempts"]
stealsTimeTaken = stats["stealsTimeTaken"]

listsByNumFailed = [[] for x in range(max(numFailedAttempts)+1)]
for i in range(len(numFailedAttempts)):
	listsByNumFailed[numFailedAttempts[i]].append(stealsTimeTaken[i])

xs = []
ys = []

for i in range(len(listsByNumFailed)):
	count = len(listsByNumFailed[i])
	output = str(i) + ": " + str(count)
	if count > 0:
		avg = sum(listsByNumFailed[i])/count
		output += ", " + str(avg)
		xs.append(i)
		ys.append(avg)
	print(output)
	
plot.plot(xs, ys, 'ro')
plot.show()

#TODO: compare graphs as we gooooo