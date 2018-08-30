package android.chat.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "user_table")
public class User implements Parcelable{

   @NonNull
   public String getUserId() {
      return userId;
   }

   public void setUserId(@NonNull String userId) {
      this.userId = userId;
   }

   @NonNull
   public String getUserName() {
      return userName;
   }

   public void setUserName(@NonNull String userName) {
      this.userName = userName;
   }

   @NonNull
   public String getEmailId() {
      return emailId;
   }

   public void setEmailId(@NonNull String emailId) {
      this.emailId = emailId;
   }

   @NonNull
   public String getMobileNumber() {
      return mobileNumber;
   }

   public void setMobileNumber(@NonNull String mobileNumber) {
      this.mobileNumber = mobileNumber;
   }

   @NonNull
   public String getGender() {
      return gender;
   }

   public void setGender(@NonNull String gender) {
      this.gender = gender;
   }

   @NonNull
   public String getGroupNames() {
      return groupNames;
   }

   public void setGroupNames(@NonNull String groupNames) {
      this.groupNames = groupNames;
   }

   public User(@NonNull String userId,
               @NonNull String userName,
               @NonNull String emailId,
               @NonNull String mobileNumber,
               @NonNull String gender,
               @NonNull String groupNames) {
      this.userId = userId;
      this.userName = userName;
      this.emailId = emailId;
      this.mobileNumber = mobileNumber;
      this.gender = gender;
      this.groupNames = groupNames;
   }

   @PrimaryKey
   @NonNull
   @ColumnInfo(name = "userId")
   private String userId;

   @NonNull
   @ColumnInfo(name = "userName")
   private String userName;

   @NonNull
   @ColumnInfo(name = "emailId")
   private String emailId;

   @NonNull
   @ColumnInfo(name = "mobileNumber")
   private String mobileNumber;

   @NonNull
   @ColumnInfo(name = "gender")
   private String gender;

   @NonNull
   @ColumnInfo(name = "groupsAdded")
   private String groupNames;


   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(this.userId);
      dest.writeString(this.userName);
      dest.writeString(this.emailId);
      dest.writeString(this.mobileNumber);
      dest.writeString(this.gender);
      dest.writeString(this.groupNames);
   }

   protected User(Parcel in) {
      this.userId = in.readString();
      this.userName = in.readString();
      this.emailId = in.readString();
      this.mobileNumber = in.readString();
      this.gender = in.readString();
      this.groupNames = in.readString();
   }

   public static final Creator<User> CREATOR = new Creator<User>() {
      @Override
      public User createFromParcel(Parcel source) {
         return new User(source);
      }

      @Override
      public User[] newArray(int size) {
         return new User[size];
      }
   };
}