/**
 * Copyright 2012 Sandy Ryza
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package pls.vrp;

import JaCoP.core.Domain;
import JaCoP.core.IntDomain;
import JaCoP.core.IntVar;
import JaCoP.search.Indomain;

public class IndomainShortest<T extends IntVar> implements Indomain<T> {
	
	private IntVar[] nodePositions;
	private IntVar[] successors;
	private IntVar[] nodesInOrder;
	
	@Override
	public int indomain(T var) {
		IntDomain dom  = var.dom();
		//TODO: can make this more optimal later
		for (int val : dom.toIntArray()) {
			//val is the index of a node which code be this node's successor
			//we want to assign it one
			
		}
		// TODO Auto-generated method stub
		return 0;
	}

}
