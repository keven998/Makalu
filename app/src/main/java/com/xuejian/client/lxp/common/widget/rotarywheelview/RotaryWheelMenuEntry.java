package com.xuejian.client.lxp.common.widget.rotarywheelview;

public interface RotaryWheelMenuEntry {
	
	public String getName();
    
	public String getLabel();
    
    public int getIcon();
    
    public void menuActiviated();
    
    public int getIndex();
}
