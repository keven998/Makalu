package com.xuejian.client.lxp.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.aizou.core.utils.RegexUtils;
import com.xuejian.client.lxp.bean.AddressBookbean;
import com.xuejian.client.lxp.common.account.AccountManager;
import com.xuejian.client.lxp.common.utils.video.Utils;
import com.xuejian.client.lxp.db.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rjm on 2014/10/28.
 */
public class PhoneContactUtils {
    private static final String[] CONTACTOR_ION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    public static ArrayList<AddressBookbean> getPhoneContact(Context context) {
        ArrayList<AddressBookbean> contactList = new ArrayList<AddressBookbean>();
        AddressBookbean bean = null;
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
                String lastContactIdString = "";
                int entryId = 0;
                int sourceId = 0;
                User user = AccountManager.getInstance().getLoginAccount(context);
                while (phones.moveToNext()) {
                    bean = new AddressBookbean();
                    phoneString = phones.getString(phoneIndex);
                    if (phoneString.equals(user.getTel())) {
                        continue;
                    }
                    displayNameString = phones.getString(displayNameIndex);
                    contactIdString = phones.getString(contactIdIndex);
                    if (!lastContactIdString.equals(contactIdString)) {
                        sourceId++;
                    }
                    entryId++;

                    bean.entryId = entryId;
                    bean.sourceId = sourceId;
                    bean.name = displayNameString;
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
        return contactList;

    }


    public static ArrayList<AddressBookbean> getPhoneContactByKeyWord(Context context,List<AddressBookbean> allMember,String keyword) {
        ArrayList<AddressBookbean> contactList = new ArrayList<AddressBookbean>();
        try {
            if(allMember!=null && allMember.size()>0){
                    for(int i=0;i<allMember.size();i++){
                        if(allMember.get(i)!=null && allMember.get(i).tel.replaceAll(" ","").contains(keyword) || allMember.get(i).name.contains(keyword) || (allMember.get(i).name!=null && allMember.get(i).name.toLowerCase().contains(keyword.toLowerCase()))){
                            contactList.add(allMember.get(i));
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return contactList;
    }

}
