import matplotlib.pyplot as plot
import json
from vrpstatutils import *

runids = [i for i in range(45, 69)]
runids.extend([8, 9, 22, 23])

res = {}
for runid in runids:
	stats = load_stats(runid)
	numMappers = stats["numMappers"]
	bestSolCosts = stats["bestSolCosts"]
	bestSolCost = bestSolCosts[len(bestSolCosts)-1]
	if numMappers not in res:
		res[numMappers] = []
	res[numMappers].append(bestSolCost)
	
xs = [numMappers for numMappers in sorted(res.keys())]
ys = [1.0 * sum(res[numMappers]) / len(res[numMappers]) for numMappers in xs]

plot.plot(xs, ys, '-o')
plot.xlabel("Number of Mappers")
plot.ylabel("Solution Cost")
plot.show()