package com.xuejian.client.lxp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yibiao.qin on 2015/12/2.
 */
public class TravellerEntity implements Parcelable {
    private String surname;
    private String givenName;
    private String gender;
    private String email;
    /**
     * dialCode : 86
     * number : 13099887766
     */

    private TelBean tel;
    /**
     * number : 1201011999....
     * idType : chineseID
     */

    private List<IdentityBean> identities;

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTel(TelBean tel) {
        this.tel = tel;
    }

    public void setIdentities(List<IdentityBean> identities) {
        this.identities = identities;
    }

    public String getSurname() {
        return surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public TelBean getTel() {
        return tel;
    }

    public List<IdentityBean> getIdentities() {
        return identities;
    }

    public static class TelEntity {
        private int dialCode;
        private long number;

        public void setDialCode(int dialCode) {
            this.dialCode = dialCode;
        }

        public void setNumber(long number) {
            this.number = number;
        }

        public int getDialCode() {
            return dialCode;
        }

        public long getNumber() {
            return number;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.surname);
        dest.writeString(this.givenName);
        dest.writeString(this.gender);
        dest.writeString(this.email);
        dest.writeParcelable(this.tel, 0);
        dest.writeTypedList(identities);
    }

    public TravellerEntity() {
    }

    protected TravellerEntity(Parcel in) {
        this.surname = in.readString();
        this.givenName = in.readString();
        this.gender = in.readString();
        this.email = in.readString();
        this.tel = in.readParcelable(TelBean.class.getClassLoader());
        this.identities = in.createTypedArrayList(IdentityBean.CREATOR);
    }

    public static final Parcelable.Creator<TravellerEntity> CREATOR = new Parcelable.Creator<TravellerEntity>() {
        public TravellerEntity createFromParcel(Parcel source) {
            return new TravellerEntity(source);
        }

        public TravellerEntity[] newArray(int size) {
            return new TravellerEntity[size];
        }
    };
}