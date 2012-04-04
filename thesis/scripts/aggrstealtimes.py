import json
import os
import sys

job_id = sys.argv[1]
stats_dir = "/home/sryza/logs/stats/"

vassal_id = int(sys.argv[2])
total_steal_times = []
total_steal_times_total = 0
while True:
	stats_file = stats_dir + str(vassal_id) + "_" + job_id + ".stats"
	if not os.path.exists(stats_file):
		print(stats_file + " does not exist");
		break
		
	f = open(stats_file)
	try:
		stats = json.load(f)
	
		total_steal_time = stats["totalStealTime"]
		total_steal_times.append(total_steal_time)
		total_steal_times_total += total_steal_time
	except ValueError:
		print("Failed to parse " + stats_file)
	
	vassal_id += 1

print total_steal_times
print total_steal_times_total
