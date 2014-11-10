package com.aizou.peachtravel.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/11/8.
 */
public class AddressBookbean {
    public int entryId;
    public int sourceId;
    public String name;
    public List<String> tel=new ArrayList<String>();
    public List<String> email=new ArrayList<String>();
    public List<String> weixin= new ArrayList<String>();
}
