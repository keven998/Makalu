/**
 * ****************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package com.xuejian.client.lxp.common.widget.freeflow.core;


import com.xuejian.client.lxp.common.widget.freeflow.layouts.FreeFlowLayout;

/**
 * Interface that all listeners interested in layout change events must
 * implement
 *
 */
public interface FreeFlowEventListener {

    /**
     * Event dispatched when layout change animations are about to begin.
     */
    void layoutChangeAnimationsStarting();

    void layoutChangeAnimationsComplete();

    void layoutComputed();

    /**
     * Dispatched when the underlying data has been changed
     */
    void dataChanged();

    /**
     * Called when the layout is about to change. Measurements based on the
     * current data provider and current size have been completed.
     *
     * @param oldLayout
     * @param newLayout
     */
    void onLayoutChanging(FreeFlowLayout oldLayout, FreeFlowLayout newLayout);

    /**
     * Dispatched when onLayout is called and views are laid out. Note that onLayout
     * could be called because of views moving to new positions during a transition
     * animation, so you are passed that value as a boolean.
     *
     * @param areTransitionAnimationsPlaying Whether layout transition animations are
     * playing
     */
    void layoutComplete(boolean areTransitionAnimationsPlaying);


}
