import json
import os
import sys

job_id = sys.argv[1]
stats_dir = "/home/sryza/logs/stats/"

vassal_id = 1
next_node_counts = []
while True:
	stats_file = stats_dir + str(vassal_id) + "_" + job_id + ".stats"
	if not os.path.exists(stats_file):
		break
		
	f = open(stats_file)
	stats = json.load(f)
	
	next_node_count = stats["nextNode_latencies_stats"]["count"]
	next_node_counts.append(next_node_count)
	
	vassal_id += 1

print next_node_counts
print sum(next_node_counts)
