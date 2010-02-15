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

package org.ow2.util.progress;

/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 28 févr. 2009
 * Time: 23:49:47
 * To change this template use File | Settings | File Templates.
 */
public interface IProgressMonitor {

    /**
     * Return a locally (on a given monitor's scope) unique identifier
     * @return a unique identifier
     */
    String getId();

    /**
     * Set the tick count to be worked by this progress monitor.
     * It is an error to call this method more than once.
     * @param count number of items to be worked
     */
    void setConsumableWorkUnits(int count);

    /**
     * @return the total of work units decided for this monitor.
     */
    int getConsumableWorkUnits();

    /**
     * Creates a sub monitor that will (once it is complete) count for the given number of ticks.
     * For example:
     * <code>IProgressMonitor sub = monitor.createSubProgressMonitor(10);</code>
     * will returns a new ProgressMonitor that will count for 10 ticks of the parent ProgressMonitor.
     * @param tickCount ticks count in the parent scale.
     * @return a child progress monitor
     */
    IProgressMonitor createSubProgressMonitor(int tickCount);

    /**
     * Work for the given number of ticks.
     * @param tickCount work units count.
     */
    void consume(int tickCount);

    /**
     * Work for the given number of ticks.
     * @param tickCount work units count.
     * @param message feedback message
     */
    void consume(int tickCount, String message);

    /**
     * Finish the task (even if it's not 100% completed).
     * @param message descriptive message
     */
    void finish(String message);

    /**
     * Cancel the current task.
     * @param message descriptive message
     */
    void cancel(String message);

    /**
     * Add a new IProgressListener that will be notified of progress.
     * @param listener Progress listener that will be notified on ProgressEvents
     */
    void addProgressListener(IProgressListener listener);

    /**
     * Remove an IProgressListener. After this call, the listener will no
     * more be notified of IProgressEvents.
     * @param listener removed listener
     */
    void removeProgressListener(IProgressListener listener);

}
