import matplotlib.pyplot as plot
import json
from vrpstatutils import *

def get_xs_and_ys(runids):
	res = {}
	for runid in runids:
		stats = load_stats(runid)
		numMappers = stats["numMappers"]
		totalWorkingTimes = stats["totalWorkingTimes"]
		if numMappers not in res:
			res[numMappers] = []
		totalTime = sum(stats["roundLengths"])
		ideal = totalTime * numMappers
		res[numMappers].append(1.0 * sum(totalWorkingTimes) / ideal)
		
	xs = [numMappers for numMappers in sorted(res.keys())]
	ys = [1.0 * sum(res[numMappers]) / len(res[numMappers]) for numMappers in xs]
	return xs, ys

runids = [i for i in range(45, 69)]
runids.extend([8, 9, 22, 23])

xs, ys = get_xs_and_ys(runids)
plot.plot(xs, ys, '-o')

runids = [i for i in range(69, 93)]

xs, ys = get_xs_and_ys(runids)
plot.plot(xs, ys, '-o')

plot.xlabel("Number of Mappers")
plot.ylabel("Total Mapping Time / Ideal Time")
plot.show()