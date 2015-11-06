package com.xuejian.client.lxp.module.RNView;

import com.aizou.core.widget.DotView;
import com.facebook.react.uimanager.CatalystStylesDiffMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIProp;

/**
 * Created by yibiao.qin on 2015/10/27.
 */
public class ReactDotViewManager extends SimpleViewManager<DotView> {
    //    @UIProp(UIProp.Type.STRING)
//    public static final String Dot_radius = "dot_radius";
//    @UIProp(UIProp.Type.STRING)
//    public static final String dot_selected_color = "dot_selected_color";
//    @UIProp(UIProp.Type.STRING)
//    public static final String dot_unselected_color = "dot_unselected_color";
    @UIProp(UIProp.Type.STRING)
    public static final String PROP_SRC = "src";
    @UIProp(UIProp.Type.NUMBER)
    public static final String NUM = "dotNum";

    @Override
    public String getName() {
        return "DotView";
    }

    @Override
    protected DotView createViewInstance(ThemedReactContext reactContext) {
        return new DotView(reactContext);
    }

    @Override
    protected void updateView(DotView view, CatalystStylesDiffMap props) {
        super.updateView(view, props);
        if (props.hasKey(NUM)) {
            view.setNum(props.getInt(NUM,3));
        }
    }
}
