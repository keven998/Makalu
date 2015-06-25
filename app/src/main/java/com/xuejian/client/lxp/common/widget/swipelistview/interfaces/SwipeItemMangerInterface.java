package com.xuejian.client.lxp.common.widget.swipelistview.interfaces;


import com.xuejian.client.lxp.common.widget.swipelistview.SwipeLayout;
import com.xuejian.client.lxp.common.widget.swipelistview.util.Attributes;

import java.util.List;

public interface SwipeItemMangerInterface {

    void openItem(int position);

    void closeItem(int position);

    void closeAllExcept(SwipeLayout layout);
    
    void closeAllItems();

    List<Integer> getOpenItems();

    List<SwipeLayout> getOpenLayouts();

    void removeShownLayouts(SwipeLayout layout);

    boolean isOpen(int position);

    Attributes.Mode getMode();

    void setMode(Attributes.Mode mode);
}
