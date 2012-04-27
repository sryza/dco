from vrpstatutils import *
import matplotlib.pyplot as plot
import sys

runids = [sys.argv[1]]

for runid in runids:
	stats = load_stats(runid)
	helperTimes = stats["helperTimes"][2:]
	helperImprovements = stats["helperImprovements"][2:]
	regularTimes = stats["regularTimes"][2:]
	regularImprovements = stats["regularImprovements"][2:]
	
	xs = []
	ys1 = []
	ys2 = []
	for i in range(len(helperTimes)):
		if helperTimes[i] == 0:
			ratio = 0
		else:
			ratio = helperImprovements[i] / helperTimes[i]
		ratioReg = regularImprovements[i] / regularTimes[i]
		xs.append(i)
		ys1.append(ratio)
		ys2.append(ratioReg)

	print(1.0 * sum(helperImprovements) / sum(helperTimes))
	print(1.0 * sum(regularImprovements) / sum(regularTimes))
	
	plot.plot(xs, ys1, '-o')
	plot.plot(xs, ys2, '-o')
	plot.xlabel("Round")
	plot.ylabel("Improvement / Time (ms)")
	plot.legend(["Helper Neighborhoods", "Regular (Random) Neighborhoods"])
	plot.show()

