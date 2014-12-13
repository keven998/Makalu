package com.aizou.peachtravel.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.aizou.peachtravel.bean.AddressBookbean;
import com.aizou.peachtravel.bean.PhoneContactBean;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/10/28.
 */
public class PhoneContactUtils {
    private static final String[] CONTACTOR_ION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static ArrayList<AddressBookbean> getPhoneContact(Context context){
        ArrayList<AddressBookbean> contactList = new ArrayList<AddressBookbean>();
        AddressBookbean bean = null;
        long startTime=System.currentTimeMillis();
        Log.d("time", "start = " + startTime);
        Cursor phones = null;
        ContentResolver cr = context.getContentResolver();
        try {
            phones = cr
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , CONTACTOR_ION, null, null, "sort_key");

            if (phones != null) {
                final int contactIdIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                final int displayNameIndex = phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneString, displayNameString, contactIdString;
                while (phones.moveToNext()) {
                    bean= new AddressBookbean();
                    phoneString = phones.getString(phoneIndex);
                    displayNameString = phones.getString(displayNameIndex);
                    contactIdString = phones.getString(contactIdIndex);
                    bean.name =displayNameString;
                    bean.tel = phoneString;
                    contactList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (phones != null)
                phones.close();
        }
        long endTime = System.currentTimeMillis();
        Log.d("time", "end = " + endTime);
        Log.d("time", "costTime = " + (endTime-startTime));
        return  contactList;

    }


}
