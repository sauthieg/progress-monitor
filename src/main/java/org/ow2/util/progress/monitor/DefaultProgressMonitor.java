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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.util.progress.IProgressListener;
import org.ow2.util.progress.IProgressMonitor;
import org.ow2.util.progress.ProgressEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 1 mars 2009
 * Time: 10:24:16
 * To change this template use File | Settings | File Templates.
 */
public class DefaultProgressMonitor implements IProgressMonitor, IProgressListener {

    /**
     * Number of work units defined at startup.
     */
    private int workUnitsTotal = -1;

    /**
     * Current number of completed work units.
     */
    private int completedWorkUnits = 0;

    private Map<String, SubCounter> childsCounters;

    private List<IProgressListener> listeners;
    
    private String id;

    private int idCounter = 0;


    /**
     * Close means that the monitor has reach 100%
     */
    private boolean closed = false;

    public DefaultProgressMonitor(String id) {
        this.id = id;
        this.listeners = new ArrayList<IProgressListener>();
        this.childsCounters = new HashMap<String, SubCounter>();
    }

    /**
     * Return a locally (on a given monitor's scope) unique identifier
     *
     * @return a unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Set the tick count to be worked by this progress monitor.
     * It is an error to call this method more than once.
     *
     * @param count number of items to be worked
     */
    public void setConsumableWorkUnits(int count) {
        if (workUnitsTotal != -1) {
            throw new IllegalStateException("Cannot assign tick count twice");
        }
        this.workUnitsTotal = count;
    }

    /**
     * Creates a sub monitor that will (once it is complete) count for the given number of ticks.
     * For example:
     * <code>IProgressMonitor sub = monitor.createSubProgressMonitor(10);</code>
     * will returns a new ProgressMonitor that will count for 10 ticks of the parent ProgressMonitor.
     *
     * @param tickCount ticks count in the parent scale.
     * @return a child progress monitor
     */
    public IProgressMonitor createSubProgressMonitor(int tickCount) {
        String name = "Child-" + idCounter++;
        IProgressMonitor child = new DefaultProgressMonitor(name);
        child.addProgressListener(this);
        childsCounters.put(name, new SubCounter(tickCount));
        return child;
    }

    /**
     * Work for the given number of ticks.
     *
     * @param tickCount work units count.
     */
    public void consume(int tickCount) {
        this.completedWorkUnits += tickCount;
        fireWorkUnitsConsumed("");

        if (completedWorkUnits == workUnitsTotal) {
            fireWorkCompleted("");
        }
    }

    private void fireWorkCompleted(String message) {
        ProgressEvent event = new ProgressEvent(this, completedWorkUnits, message);
        for (IProgressListener listener : listeners) {
            listener.onWorkCompleted(event);
        }

        this.closed = true;
    }

    /**
     * Work for the given number of ticks.
     *
     * @param tickCount work units count.
     * @param message   feedback message
     */
    public void consume(int tickCount, String message) {
        this.completedWorkUnits += tickCount;
        fireWorkUnitsConsumed(message);

        if (completedWorkUnits == workUnitsTotal) {
            fireWorkCompleted(message);
        }
    }

    private void fireWorkUnitsConsumed(String message) {

        ProgressEvent event = new ProgressEvent(this, sumWorkedUnits(), message);
        for (IProgressListener listener : listeners) {
            listener.onWorkUnitsConsumed(event);
        }
    }

    private double sumWorkedUnits() {
        double childsWorkedUnits = 0;
        for(SubCounter counter : childsCounters.values()) {
            double total = counter.getTotal();
            double worked = counter.getConsumedUnits();
            double childTotal = counter.getChildTotal();

            double normalized = (worked / childTotal) * total;

            childsWorkedUnits += normalized;
        }
        return completedWorkUnits + childsWorkedUnits;
    }

    /**
     * Finish the task (even if it's not 100% completed).
     *
     * @param message descriptive message
     */
    public void finish(String message) {
        fireWorkCompleted("");
    }

    /**
     * Cancel the current task.
     *
     * @param message descriptive message
     */
    public void cancel(String message) {
        fireWorkCanceled(message);
    }

    private void fireWorkCanceled(String message) {
        ProgressEvent event = new ProgressEvent(this, sumWorkedUnits(), message);
        for (IProgressListener listener : listeners) {
            listener.onCancel(event);
        }

    }

    /**
     * Add a new IProgressListener that will be notified of progress.
     *
     * @param listener Progress listener that will be notified on ProgressEvents
     */
    public void addProgressListener(IProgressListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Remove an IProgressListener. After this call, the listener will no
     * more be notified of IProgressEvents.
     *
     * @param listener removed listener
     */
    public void removeProgressListener(IProgressListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * @return the total of work units decided for this monitor.
     */
    public int getConsumableWorkUnits() {
        return workUnitsTotal;
    }

    public void onWorkCompleted(ProgressEvent event) {

        String id = event.getSource().getId();
        SubCounter counter = this.childsCounters.remove(id);

        this.completedWorkUnits += counter.getTotal();

        fireWorkUnitsConsumed("Child Progress monitor is closed.");
        if (completedWorkUnits == workUnitsTotal) {
            fireWorkCompleted("");
        }
    }

    public void onWorkUnitsConsumed(ProgressEvent event) {
        String id = event.getSource().getId();
        SubCounter counter = this.childsCounters.get(id);
        double units = event.getConsumedUnits();
        counter.setConsumedUnits(units);
        counter.setChildTotal(event.getSource().getConsumableWorkUnits());

        fireWorkUnitsConsumed("Child Progress monitor has new values.");
        if (completedWorkUnits == workUnitsTotal) {
            fireWorkCompleted("");
        }
    }

    public void onCancel(ProgressEvent event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class SubCounter {
        private int total;
        private double consumedUnits;
        private int childTotal;

        public SubCounter(int total) {
            this.total = total;
            this.consumedUnits = 0.0d;
            this.childTotal = 0;
        }

        public double getConsumedUnits() {
            return consumedUnits;
        }

        public int getTotal() {
            return total;
        }

        public int getChildTotal() {
            return childTotal;
        }

        public void setConsumedUnits(double consumedUnits) {
            this.consumedUnits = consumedUnits;
        }

        public void setChildTotal(int childTotal) {
            this.childTotal = childTotal;
        }
    }
}
