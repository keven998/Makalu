package com.xuejian.client.lxp.common.widget.TagView;

import java.io.Serializable;

public class Tag implements Serializable {

    private static final long serialVersionUID = 2684657309332033242L;

    private int backgroundResId;
    private int id;
    private boolean isChecked;
    private int leftDrawableResId;
    private int rightDrawableResId;
    private String title;
    private int textColor;
    private int tagViewResId;
    public Tag() {

    }

    public Tag(int paramInt, String paramString) {
        this.id = paramInt;
        this.title = paramString;
    }

    public int getBackgroundResId() {
        return this.backgroundResId;
    }

    public int getId() {
        return this.id;
    }

    public int getLeftDrawableResId() {
        return this.leftDrawableResId;
    }

    public int getRightDrawableResId() {
        return this.rightDrawableResId;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setBackgroundResId(int paramInt) {
        this.backgroundResId = paramInt;
    }

    public void setChecked(boolean paramBoolean) {
        this.isChecked = paramBoolean;
    }

    public void setId(int paramInt) {
        this.id = paramInt;
    }

    public void setLeftDrawableResId(int paramInt) {
        this.leftDrawableResId = paramInt;
    }

    public void setRightDrawableResId(int paramInt) {
        this.rightDrawableResId = paramInt;
    }

    public void setTitle(String paramString) {
        this.title = paramString;
    }

    public int getTextColor() {
        return textColor;
    }
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTagViewResId() {
        return tagViewResId;
    }

    public void setTagViewResId(int tagViewResId) {
        this.tagViewResId = tagViewResId;
    }
}

