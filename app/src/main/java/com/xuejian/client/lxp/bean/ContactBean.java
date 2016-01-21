package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yibiao.qin on 2015/12/1.
 */
public class ContactBean implements Parcelable {

    private String surname;
    private String givenName;
    private TelBean tel;
    private String email;

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setTel(TelBean tel) {
        this.tel = tel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public TelBean getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.surname);
        dest.writeString(this.givenName);
        dest.writeParcelable(this.tel, 0);
        dest.writeString(this.email);
    }

    public ContactBean() {
    }

    protected ContactBean(Parcel in) {
        this.surname = in.readString();
        this.givenName = in.readString();
        this.tel = in.readParcelable(TelBean.class.getClassLoader());
        this.email = in.readString();
    }

    public static final Parcelable.Creator<ContactBean> CREATOR = new Parcelable.Creator<ContactBean>() {
        public ContactBean createFromParcel(Parcel source) {
            return new ContactBean(source);
        }

        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };
}
