package bnb.tsp.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import bnb.rpc.Ports;
import bnb.vassal.LordProxy;
import bnb.vassal.VassalRunner;

public class VassalMain {
	
	private static final Logger LOG = Logger.getLogger(VassalMain.class);
	
	private static final String DEFAULT_LORD_HOST = "localhost";
	private static final int DEFAULT_NUM_SLOTS = 1;
	private static final int DEFAULT_LORD_PORT = Ports.DEFAULT_LORD_PORT;
	private static final int DEFAULT_VASSAL_PORT = Ports.DEFAULT_VASSAL_PORT;
	
	public static void main(String[] args) throws IOException {
		String lordHost = args[0];
		int id = Integer.parseInt(args[1]);
		int lordPort = DEFAULT_LORD_PORT;
		int vassalPort = DEFAULT_VASSAL_PORT;
		int numSlots = Integer.parseInt(args[2]);
		int testId = Integer.parseInt(args[3]);
		
		Appender appender = (Appender)Logger.getRootLogger().getAllAppenders().nextElement();
		Logger.getRootLogger().removeAllAppenders();
		String logFilePath = "logs/vassal" + id + ".log";
		new File(logFilePath).delete();
		FileAppender fileAppender = new FileAppender(appender.getLayout(), logFilePath);
		Logger.getRootLogger().addAppender(fileAppender);
		
		//create output file for stats
		String statsFileDir = "logs/stats/";
		File statsFile = new File(statsFileDir + id + "_" + testId + ".stats");

		FileOutputStream sfos = new FileOutputStream(statsFile);
		
		LordProxy lordProxy = new LordProxy(lordHost, lordPort);
		LOG.info("created lord proxy");
		VassalRunner vassal = new VassalRunner(lordProxy, numSlots, id, vassalPort, sfos);
		LOG.info("about to start vassal runner");
		vassal.start();
	}
}
