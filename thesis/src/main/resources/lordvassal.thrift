namespace java bnb.rpc

struct ThriftData {
	1:string className,
	2:binary bytes
}

service ThriftVassal {
	void updateBestSolCost(1:double bestCost, 2:i32 jobid),
	void startJobTasks(1:list<ThriftData> nodeData, 2:ThriftData problemData, 3:double bestCost, 4:i32 jobid, 5:i32 nthreads),
	list<ThriftData> stealWork(1:i32 jobid),
	i32 getNumSlots(),
	i32 getVassalId()
}

service ThriftLord {
	void sendBestSolCost(1:double bestCost, 2:i32 jobid, 3:i32 vassalid, 4:ThriftData solution),
	list<ThriftData> askForWork(1:i32 jobid, 2:i32 vassalid, 3:double bestCost),
	void registerVassal(1:string hostname, 2:i32 port, 3:i32 vassalid)
}