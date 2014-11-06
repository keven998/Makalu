package com.aizou.peachtravel.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.aizou.peachtravel.bean.PhoneContactBean;

import java.util.ArrayList;

/**
 * Created by Rjm on 2014/10/28.
 */
public class PhoneContactUtils {
    public static ArrayList<PhoneContactBean> getPhoneContact(Context context){
        ArrayList<PhoneContactBean> contactList = new ArrayList<PhoneContactBean>();
        int nameIndex=-1;
        ContentResolver cr=context.getContentResolver();
        Cursor cur=cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,null);
        String name;
        PhoneContactBean bean = null;
        while(cur.moveToNext()){
            //得到名字
            name="";
            nameIndex=cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            name=cur.getString(nameIndex);
            //得到电话号码
            String contactId = cur.getString(cur
                    .getColumnIndex(ContactsContract.Contacts._ID)); // 获取联系人的ID号，在SQLite中的数据库ID
            Cursor phoneCursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                bean =new PhoneContactBean();
                String strPhoneNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); // 手机号码字段联系人可能不止一个
                bean.name = name;
                bean.phone = strPhoneNumber;
                contactList.add(bean);

            }
            phoneCursor.close();


        }

        cur.close();
        return  contactList;

    }


}
