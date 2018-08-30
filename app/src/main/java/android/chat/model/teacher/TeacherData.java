package android.chat.model.teacher;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sibaprasad on 07/04/18.
 */

public class TeacherData implements Parcelable {
    String teacherId;
    String name;
    String subjects;

    public String getSubjects() {
        return subjects;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public static Creator<TeacherData> getCREATOR() {
        return CREATOR;
    }

    public TeacherData(String teacherId, String name, String emailId,
                       String mobileNumber,
                       String subjects,
                       String password) {
        this.teacherId = teacherId;
        this.name = name;
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.year = year;
        this.password = password;

        this.subjects = subjects;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String studentId) {
        this.teacherId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String emailId;
    String mobileNumber;
    String year;
    String password;


    public TeacherData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.teacherId);
        dest.writeString(this.name);
        dest.writeString(this.subjects);
        dest.writeString(this.emailId);
        dest.writeString(this.mobileNumber);
        dest.writeString(this.year);
        dest.writeString(this.password);
    }

    protected TeacherData(Parcel in) {
        this.teacherId = in.readString();
        this.name = in.readString();
        this.subjects = in.readString();
        this.emailId = in.readString();
        this.mobileNumber = in.readString();
        this.year = in.readString();
        this.password = in.readString();
    }

    public static final Creator<TeacherData> CREATOR = new Creator<TeacherData>() {
        @Override
        public TeacherData createFromParcel(Parcel source) {
            return new TeacherData(source);
        }

        @Override
        public TeacherData[] newArray(int size) {
            return new TeacherData[size];
        }
    };
}
