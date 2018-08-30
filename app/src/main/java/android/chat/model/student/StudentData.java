package android.chat.model.student;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sibaprasad on 07/04/18.
 */

public class StudentData implements Parcelable {
    String studentId;
    String name;

    public StudentData(String studentId, String name,
                       String emailId, String mobileNumber,
                       String year, String password) {
        this.studentId = studentId;
        this.name = name;
        this.emailId = emailId;
        this.mobileNumber = mobileNumber;
        this.year = year;
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.studentId);
        dest.writeString(this.name);
        dest.writeString(this.emailId);
        dest.writeString(this.mobileNumber);
        dest.writeString(this.year);
        dest.writeString(this.password);
    }

    public StudentData() {
    }

    protected StudentData(Parcel in) {
        this.studentId = in.readString();
        this.name = in.readString();
        this.emailId = in.readString();
        this.mobileNumber = in.readString();
        this.year = in.readString();
        this.password = in.readString();
    }

    public static final Creator<StudentData> CREATOR = new Creator<StudentData>() {
        @Override
        public StudentData createFromParcel(Parcel source) {
            return new StudentData(source);
        }

        @Override
        public StudentData[] newArray(int size) {
            return new StudentData[size];
        }
    };
}
