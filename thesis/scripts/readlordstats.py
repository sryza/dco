import json

logdir = "/home/sryza/logs/"
for i in range(59, 92):
	filename = logdir + "lord" + str(i) + ".log"
	stats = json.load(open(filename, 'r'))
	print(stats["numWorkThefts"])
	#stats["numWorkThefts"]
