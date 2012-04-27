import matplotlib.pyplot as plot
import json
import sys

stats = json.load(sys.stdin)
bestSolCosts = stats["bestSolCosts"]
jobStats = stats["jobStats"]
roundLengths = jobStats["roundLengths"]
roundFinishTimes = [0]
prevFinishTime = 0
for roundLength in roundLengths:
	prevFinishTime += roundLength
	roundFinishTimes.append(prevFinishTime);

plot.plot(roundFinishTimes, bestSolCosts)
