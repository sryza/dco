import matplotlib.pyplot as plot
from vrpstatutils import *

runids = [111, 45]

for runid in runids:
	stats = load_stats(runid)
	bestSolCosts = stats["bestSolCosts"]
	roundTimes = stats["roundLengths"]
	cumRoundTimes = [0]
	for i in range(1, len(roundTimes)):
		cumRoundTimes.append(cumRoundTimes[i-1] + roundTimes[i])
	
	plot.plot(cumRoundTimes[1:], bestSolCosts[1:], '-o')

plot.xlabel("Time (ms)")
plot.ylabel("Solution Cost")
plot.show()