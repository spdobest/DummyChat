package android.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserOrGroupDetails implements Parcelable {

    public String name;
    public String email;
    public String userid;
    public String number;
    public String subjectList;
    public boolean isGroup;

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public UserOrGroupDetails() {
    }

    public UserOrGroupDetails(String userId, String name, String email, String number, String subjectList) {
        this.name = name;
        this.email = email;
        this.userid = userId;
        this.number = number;
        this.subjectList = subjectList;
    }

    public String getSubjectList() {
        return subjectList;
    }

    public void setSubjectList(String subjectList) {
        this.subjectList = subjectList;
    }

    public static Creator<UserOrGroupDetails> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.userid);
        dest.writeString(this.number);
        dest.writeString(this.subjectList);
        dest.writeByte(this.isGroup ? (byte) 1 : (byte) 0);
    }

    protected UserOrGroupDetails(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.userid = in.readString();
        this.number = in.readString();
        this.subjectList = in.readString();
        this.isGroup = in.readByte() != 0;
    }

    public static final Creator<UserOrGroupDetails> CREATOR = new Creator<UserOrGroupDetails>() {
        @Override
        public UserOrGroupDetails createFromParcel(Parcel source) {
            return new UserOrGroupDetails(source);
        }

        @Override
        public UserOrGroupDetails[] newArray(int size) {
            return new UserOrGroupDetails[size];
        }
    };
}
