package bnb.vassal;

import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import bnb.rpc.Ports;

public class VassalMain {
	
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
		
		Appender appender = (Appender)Logger.getRootLogger().getAllAppenders().nextElement();
		Logger.getRootLogger().removeAllAppenders();
		FileAppender fileAppender = new FileAppender(appender.getLayout(), "logs/vassal" + id + ".log");
		Logger.getRootLogger().addAppender(fileAppender);
		
		LordProxy lordProxy = new LordProxy(lordHost, lordPort);
		VassalRunner vassal = new VassalRunner(lordProxy, numSlots, id, vassalPort);
		vassal.start();
	}
}
