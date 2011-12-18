package bnb;

import bnb.lord.LordRunner;
import bnb.rpc.Ports;
import bnb.vassal.LordProxy;
import bnb.vassal.VassalRunner;

public class SimpleTest {
	public static void main(String[] args) {
		
		//start lord
		LordRunner lord = new LordRunner(Ports.DEFAULT_LORD_PORT);
		lord.start();
		
		//start vassals
		final int NUM_VASSALS = 2;
		VassalRunner[] vassals = new VassalRunner[NUM_VASSALS];
		for (int i = 0; i < vassals.length; i++) {
			int port = 1455 + i;
			int id = i;
//			vassals[i] = new VassalRunner(makeLordProxy(), 1, id, port);
			vassals[i].start();
		}
	}
	
	private static LordProxy makeLordProxy() {
		return new LordProxy("localhost", Ports.DEFAULT_LORD_PORT);
	}
}
