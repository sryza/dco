import subprocess
import sys
import json
from vrpstatutils import *

rangestart = int(sys.argv[1])
rangeend = int(sys.argv[2])

statses = []
for i in range(rangestart, rangeend+1):
	stats = load_stats(i)
	statses.append(stats)

print()
for stats in statses:
        numMappers = stats["numMappers"]
        k = stats["populationK"]
        bestSolCosts = stats["bestSolCosts"]
        bestSolSum = sum(bestSolCosts[1:])
        bestSolCost = bestSolCosts[len(bestSolCosts)-1]
        roundTime = stats["lsRunTime"]
        numNeighborhoods = stats["numHelperNeighborhoods"]
        numRounds = stats["numRounds"]
        problemName = stats["problemName"]
        roundTimes = stats["roundLengths"]
        totalTime = sum(roundTimes)
        totalWorkingTimes = stats["totalWorkingTimes"]
        print(str(numMappers) + "," + str(numRounds) + "," + str(k) + "," + str(roundTime) + "," + problemName + "," + str(numNeighborhoods) + ",TRUE," + str(bestSolCost) + "," + str(bestSolSum) + "," + str(totalTime) + "," + str(sum(totalWorkingTimes)))

