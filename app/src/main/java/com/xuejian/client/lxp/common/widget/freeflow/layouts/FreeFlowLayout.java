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
package com.xuejian.client.lxp.common.widget.freeflow.layouts;


import com.xuejian.client.lxp.common.widget.freeflow.core.FreeFlowItem;
import com.xuejian.client.lxp.common.widget.freeflow.core.SectionedAdapter;

import java.util.Map;

/**
 * The base class for all custom layouts. The Layout is responsible for figuring
 * out all the positions for all the views created by the Container based on the
 * <code>SectionedAdapter</code> supplied to it.
 *
 */
public interface FreeFlowLayout {

    /**
     * Called whenever Container's onMeasure is triggered Note: We don't support
     * margin and padding yet, so the dimensions are the entire actual of the
     * Container. Note that setDimensions can be called multiple times, so don't
     * use it to recompute your frames, use computeLayout instead
     *
     * @param measuredWidth
     *            The width of the Container
     * @param measuredHeight
     *            The height of the Container
     */
    void setDimensions(int measuredWidth, int measuredHeight);

    void setAdapter(SectionedAdapter adapter);

    /**
     * Generate the item proxies of all views in the given viewport, you provide
     * the left and top, the width and height are resolved from the dimensions
     * passed earlier in the setDimensions call. <br>
     * <br>
     *
     * NOTE: Any item proxies returned by this method will be rendered, sized,
     * laid out, and animated, regardeless of whether they are on screen or not.
     * So if you would like to buffer off screen items, include them in the
     * returned hashmap. <br>
     * <br>
     *
     *
     * @param viewPortLeft
     *            The left bound of the viewport
     * @param viewPortTop
     *            the top bound of the viewport
     * @return HashMap of Data to itemProxies All itemProxies returned will be
     *         renedered, sized, laid out, and animated
     */
    Map<Object, FreeFlowItem> getItemProxies(int viewPortLeft,
                                             int viewPortTop);

    void setLayoutParams(FreeFlowLayoutParams params);

    void prepareLayout();

    /**
     * Return an instance of FreeFlowItem that represents the given data. You'll
     * have to recurse through your HashMap of data items and Views and return
     * the right FreeFlowItem.
     *
     * @param item
     * @return
     */
    FreeFlowItem getFreeFlowItemForItem(Object item);

    boolean horizontalScrollEnabled();

    boolean verticalScrollEnabled();

    int getContentWidth();

    int getContentHeight();

    FreeFlowItem getItemAt(float x, float y);

    class FreeFlowLayoutParams {

    }

}
