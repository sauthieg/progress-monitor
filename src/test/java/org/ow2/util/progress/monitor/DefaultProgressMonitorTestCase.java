/**
 * Copyright 2010 OW2 Util
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ow2.util.progress.monitor;

import org.ow2.util.progress.IProgressListener;
import org.ow2.util.progress.IProgressMonitor;
import org.ow2.util.progress.ProgressEvent;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultProgressMonitorTestCase {

	private final class EmptyProgressListener implements IProgressListener {
		public boolean done;
		public double consumed;

		public void onWorkUnitsConsumed(ProgressEvent event) {
			this.consumed = event.getConsumedUnits();
		}

		public void onWorkCompleted(ProgressEvent event) {
			done = true;
		}

		public void onCancel(ProgressEvent event) {
			// TODO Auto-generated method stub
			
		}
	}

	private DefaultProgressMonitor monitor;
	
	@BeforeMethod
	private void setUp() {
		monitor = new DefaultProgressMonitor("test");
	}
	
	@Test
	public void testSimpleCompletion() {
		monitor.setConsumableWorkUnits(1);
		EmptyProgressListener listener = new EmptyProgressListener();
		monitor.addProgressListener(listener);
		monitor.consume(1);
		
		Assert.assertEquals(listener.done, true);
		
	}
	
	@Test
	public void testListenerOnWorkConsumed() {
		monitor.setConsumableWorkUnits(1);
		EmptyProgressListener listener = new EmptyProgressListener();
		monitor.addProgressListener(listener);
		monitor.consume(1);
		
		Assert.assertEquals(listener.consumed, 1d);
		
	}
	
	@Test
	public void testMultipleListenerOnWorkConsumed() {
		monitor.setConsumableWorkUnits(1);
		EmptyProgressListener listener1 = new EmptyProgressListener();
		EmptyProgressListener listener2 = new EmptyProgressListener();
		monitor.addProgressListener(listener1);
		monitor.addProgressListener(listener2);
		monitor.consume(1);
		
		Assert.assertEquals(listener1.consumed, 1d);
		Assert.assertEquals(listener2.consumed, 1d);
		
	}

	@Test
	public void testMultipleConsumption() {
		monitor.setConsumableWorkUnits(10);
		EmptyProgressListener listener = new EmptyProgressListener();
		monitor.addProgressListener(listener);
		
		monitor.consume(2);
		Assert.assertEquals(listener.consumed, 2d);
		
		monitor.consume(3);
		Assert.assertEquals(listener.consumed, 5d);
		
		monitor.consume(5);
		Assert.assertEquals(listener.consumed, 10d);
		
		Assert.assertTrue(listener.done);
		
	}
	
	@Test
	public void testSubMonitor() {
		monitor.setConsumableWorkUnits(10);
		EmptyProgressListener listener = new EmptyProgressListener();
		monitor.addProgressListener(listener);
		
		monitor.consume(2);
		Assert.assertEquals(listener.consumed, 2d);
		
		IProgressMonitor sub = monitor.createSubProgressMonitor(3);
		sub.setConsumableWorkUnits(2);
		sub.consume(2);
		Assert.assertEquals(listener.consumed, 5d);
		
		monitor.consume(5);
		Assert.assertEquals(listener.consumed, 10d);
		
		Assert.assertTrue(listener.done);
		
	}
	
}
