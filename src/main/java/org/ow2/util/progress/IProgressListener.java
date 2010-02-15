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
 * Date: 28 f�vr. 2009
 * Time: 23:51:39
 * To change this template use File | Settings | File Templates.
 */
public interface IProgressListener {

    void onWorkCompleted(ProgressEvent event);
    void onWorkUnitsConsumed(ProgressEvent event);
    void onCancel(ProgressEvent event);
}
