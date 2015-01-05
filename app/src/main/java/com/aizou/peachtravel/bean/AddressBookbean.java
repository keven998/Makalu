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
    public String tel;
    public boolean isUser;
    public boolean isContact;
    public long userId;


    public int getSort(){
        if(isContact){
            return 0;
        }else if(isUser){
            return 2;
        }else{
            return 1;
        }
    }
}
