package android.chat.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "UserGroupTable")
public class UserOrGroupDetails implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "emailId")
    public String email;

    @ColumnInfo(name = "userId")
    public String userid;

    @ColumnInfo(name = "mobileNumber")
    public String number;

    @ColumnInfo(name = "subjectList")
    public String subjectList;

    @ColumnInfo(name = "isGroup")
    public int isGroup;

    @ColumnInfo(name = "isTeacher")
    public int isTeacher;

    public int isTeacher() {
        return isTeacher;
    }

    public void setTeacher(int teacher) {
        isTeacher = teacher;
    }

    public int isGroup() {
        return isGroup;
    }

    public void setGroup(int group) {
        isGroup = group;
    }


    public UserOrGroupDetails() {
    }

    @Ignore
    public UserOrGroupDetails(String name, String email, String userid, String number,
                              String subjectList, int isGroup, int isTeacher) {
        this.name = name;
        this.email = email;
        this.userid = userid;
        this.number = number;
        this.subjectList = subjectList;
        this.isGroup = isGroup;
        this.isTeacher = isTeacher;
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

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.userid);
        dest.writeString(this.number);
        dest.writeString(this.subjectList);
        dest.writeInt(this.isGroup);
    }

    @Ignore
    protected UserOrGroupDetails(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.userid = in.readString();
        this.number = in.readString();
        this.subjectList = in.readString();
        this.isGroup = in.readInt();
    }

    @Ignore
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
