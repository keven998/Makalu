package com.aizou.peachtravel.common.widget.swipelistview.interfaces;


import com.aizou.peachtravel.common.widget.swipelistview.SwipeLayout;
import com.aizou.peachtravel.common.widget.swipelistview.util.Attributes;

import java.util.List;

public interface SwipeItemMangerInterface {

    public void openItem(int position);

    public void closeItem(int position);

    public void closeAllExcept(SwipeLayout layout);
    
    public void closeAllItems();

    public List<Integer> getOpenItems();

    public List<SwipeLayout> getOpenLayouts();

    public void removeShownLayouts(SwipeLayout layout);

    public boolean isOpen(int position);

    public com.aizou.peachtravel.common.widget.swipelistview.util.Attributes.Mode getMode();

    public void setMode(Attributes.Mode mode);
}
