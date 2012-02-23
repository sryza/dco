import json
import os
import sys

job_id = sys.argv[1]
stats_dir = "/home/sryza/logs/stats/"

vassal_id = int(sys.argv[2])
next_node_counts = []
while True:
	stats_file = stats_dir + str(vassal_id) + "_" + job_id + ".stats"
	if not os.path.exists(stats_file):
		print(stats_file + " does not exist");
		break
		
	f = open(stats_file)
	try:
		stats = json.load(f)
	
		next_node_count = stats["numEvaluated"]
		next_node_counts.append(next_node_count)
	except ValueError:
		print("Failed to parse " + stats_file)
	
	vassal_id += 1

print next_node_counts
print sum(next_node_counts)
