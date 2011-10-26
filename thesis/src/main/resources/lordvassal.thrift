namespace java bnb.rpc

struct ThriftNodeData {
	1:list<byte> bytes
}

struct ThriftProblemData {
	1:list<byte> bytes
}

service ThriftVassalProxy {
	oneway void updateBestSolCost(1:double bestCost, 2:i32 jobid),
	void startJobTasks(1:list<ThriftNodeData> nodeData, 2:ThriftProblemData problemData, 3:double bestCost, 4:i32 jobid)
}

service ThriftLordProxy {
	void sendBestSolCost(1:double bestCost, 2:i32 jobid),
	ThriftNodeData askForWork()
}