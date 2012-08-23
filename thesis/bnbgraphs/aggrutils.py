import json
import os
from operator import itemgetter

NUM_START_NODES_MAP = {1:0, 10:39, 20:39, 40:9, 50:46, 60:36, 70:26, 80:16}
logdir = "/home/sryza/logs/"
statsdir = "/home/sryza/logs/stats/"

def load_lord_stats(jobid):
	lordfilename = logdir + "lord" + str(jobid) + ".log"
	lordstats = json.load(open(lordfilename, 'r'))
	return lordstats


def load_stats(jobid, vassalid):
	"""vassalid is the first vassal id"""
	statses = []
	
	while True:
		statsfile = statsdir + str(vassalid) + "_" + str(jobid) + ".stats"
		if not os.path.exists(statsfile):
			break
		
		f = open(statsfile)
		try:
			stats = json.load(f)
			statses.append(stats)
		except ValueError:
			print("Failed to parse " + statsfile)
		
		vassalid += 1
	
	return statses
	
def load_vassal_steal_stats(jobid, vassalid):
	"""vassalid is the first vassal id"""
	vassalstatses = load_stats(jobid, vassalid)
	vassalstealtimestaken = []
	vassalstealtimes = []

	numvassals = len(vassalstatses)
	for vassalstats in vassalstatses:
		stealtimes = vassalstats["workStolenTimes"]
		timestaken = vassalstats["workStealLats"]
		stealtimes, timestaken = sort_by_list1(stealtimes, timestaken)
		vassalstealtimestaken.extend(timestaken[0:len(timestaken)-1])
		vassalstealtimes.extend(stealtimes[0:len(stealtimes)-1])
	
	vassalstealtimes, vassalstealtimestaken = sort_by_list1(vassalstealtimes, vassalstealtimestaken)
	
	vassalstealtimes = vassalstealtimes[NUM_START_NODES_MAP[numvassals]:]
	vassalstealtimestaken = vassalstealtimestaken[NUM_START_NODES_MAP[numvassals]:]
	
	return vassalstealtimes, vassalstealtimestaken, numvassals
	


def sort_by_list1(list1, list2):
	tuples = [(list1[i], list2[i]) for i in range(len(list1))]
	tuples.sort(key=itemgetter(0))
	return ([t[0] for t in tuples], [t[1] for t in tuples])

def normalize_times(timelists):
	minstart = min([timelist[0] for timelist in timelists])
	for timelist in timelists:
		for i in range(0, len(timelist)):
			timelist[i] -= minstart
