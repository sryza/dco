namespace java bnb.rpc

struct ThriftNodeData {
	1:string className,
	2:list<byte> bytes
}

struct ThriftProblemData {
	1:list<byte> bytes
}

service ThriftVassal {
	oneway void updateBestSolCost(1:double bestCost, 2:i32 jobid),
	void startJobTasks(1:list<ThriftNodeData> nodeData, 2:ThriftProblemData problemData, 3:double bestCost, 4:i32 jobid),
	list<ThriftNodeData> stealWork(1:i32 jobid),
	i32 getNumSlots()
}

service ThriftLord {
	void sendBestSolCost(1:double bestCost, 2:i32 jobid, 3:i32 vassalid),
	list<ThriftNodeData> askForWork(1:i32 jobid)
}