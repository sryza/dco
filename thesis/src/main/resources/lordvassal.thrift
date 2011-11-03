namespace java bnb.rpc

struct ThriftData {
	1:string className,
	2:list<byte> bytes
}

service ThriftVassal {
	oneway void updateBestSolCost(1:double bestCost, 2:i32 jobid),
	void startJobTasks(1:list<ThriftData> nodeData, 2:ThriftData problemData, 3:double bestCost, 4:i32 jobid),
	list<ThriftData> stealWork(1:i32 jobid),
	i32 getNumSlots()
}

service ThriftLord {
	void sendBestSolCost(1:double bestCost, 2:i32 jobid, 3:i32 vassalid),
	list<ThriftData> askForWork(1:i32 jobid)
}