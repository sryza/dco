package vrpwtw;

import org.junit.Test;

public class TestVrpBookkeeping {
	
	/**
	 * Toy example planning:
	 * Route has 4 nodes, with 3 customers to insert.
	 * At the outset,
	 * 	cust 1 can be inserted between 1 and 2 and 2 and 3,
	 * 	cust 2 can be inserted between 2 and 3,
	 *  cust 3 can be inserted between 2 and 3 and 3 and 4
	 * Cust 1 is inserted between 1 and 2, which makes it so that cust 2 can't be inserted anywhere and 
	 * cust 3 can only be inserted between 3 and 4.
	 */
	@Test
	public void testInsertAndUninsert() {
		
	}
	
	@Test
	public void testInsertAndUninsertEdgeCases() {
		
	}
	
	@Test
	public void testDistanceConstrainedFeasibility() {
		
	}
}
