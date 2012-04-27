import json

def load_stats(runid):
	contents = open(str(runid) + ".stats").read()
	return json.loads(contents[2:])