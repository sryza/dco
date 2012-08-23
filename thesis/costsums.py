import subprocess
import sys
import json
import matplotlib.pyplot as plot

costsummap = {}

for i in range(1, len(sys.argv)):
	print("./vrpstats.sh " + sys.argv[i])
	p = subprocess.Popen("./vrpstats.sh " + sys.argv[i], stdout=subprocess.PIPE, shell=True)
	contents = p.stdout.read()[2:]
	stats = json.loads(contents)
	#print(stats["bestSolCosts"])
	costsummap[int(sys.argv[i])] = sum(stats["bestSolCosts"])

xs = sorted(costsummap.keys())
ys = [costsummap[x] for x in xs]

plot.plot(xs, ys, 'o')
plot.show()



