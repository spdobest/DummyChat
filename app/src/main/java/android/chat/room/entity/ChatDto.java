package android.chat.room.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "chatData")
public class ChatDto implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;


    @ColumnInfo(name = "senderId")
    public String senderId;

    @ColumnInfo(name = "recieverId")
    public String recieverId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "senderName")
    public String senderName;

    @ColumnInfo(name = "recieverName")
    public String recieverName;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "messageType")
    public int messageType;

    @ColumnInfo(name = "PathOrUrl")
    public String PathOrUrl;

    @ColumnInfo(name = "isDownloaded")
    public int isDownloaded;

    @ColumnInfo(name = "isSent")
    public int isSent;

    @ColumnInfo(name = "extraData")
    public String extraData;

    @ColumnInfo(name = "currentTimeInMillies")
    public String currentTimeInMillies;

    @ColumnInfo(name = "chatKey")
    public String chatKey;

    @ColumnInfo(name = "chatDate")
    public String chatDate;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static Creator<ChatDto> getCREATOR() {
        return CREATOR;
    }

    @ColumnInfo(name = "subject")
    String subject;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getPathOrUrl() {
        return PathOrUrl;
    }

    public void setPathOrUrl(String pathOrUrl) {
        PathOrUrl = pathOrUrl;
    }

    public int getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public int getIsSent() {
        return isSent;
    }

    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getCurrentTimeInMillies() {
        return currentTimeInMillies;
    }

    public void setCurrentTimeInMillies(String currentTimeInMillies) {
        this.currentTimeInMillies = currentTimeInMillies;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getChatDate() {
        return chatDate;
    }

    public void setChatDate(String chatDate) {
        this.chatDate = chatDate;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    public int getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(int isTeacher) {
        this.isTeacher = isTeacher;
    }

    @ColumnInfo(name = "isAccepted")
    public int isAccepted;

    @ColumnInfo(name = "isTeacher")
    public int isTeacher;

    public ChatDto() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.senderId);
        dest.writeString(this.recieverId);
        dest.writeString(this.senderName);
        dest.writeString(this.recieverName);
        dest.writeString(this.message);
        dest.writeString(this.time);
        dest.writeInt(this.messageType);
        dest.writeString(this.PathOrUrl);
        dest.writeInt(this.isDownloaded);
        dest.writeInt(this.isSent);
        dest.writeString(this.extraData);
        dest.writeString(this.currentTimeInMillies);
        dest.writeString(this.chatKey);
        dest.writeString(this.chatDate);
        dest.writeString(this.subject);
        dest.writeInt(this.isAccepted);
        dest.writeInt(this.isTeacher);
    }

    protected ChatDto(Parcel in) {
        this.id = in.readInt();
        this.senderId = in.readString();
        this.recieverId = in.readString();
        this.senderName = in.readString();
        this.recieverName = in.readString();
        this.message = in.readString();
        this.time = in.readString();
        this.messageType = in.readInt();
        this.PathOrUrl = in.readString();
        this.isDownloaded = in.readInt();
        this.isSent = in.readInt();
        this.extraData = in.readString();
        this.currentTimeInMillies = in.readString();
        this.chatKey = in.readString();
        this.chatDate = in.readString();
        this.subject = in.readString();
        this.isAccepted = in.readInt();
        this.isTeacher = in.readInt();
    }

    public static final Creator<ChatDto> CREATOR = new Creator<ChatDto>() {
        @Override
        public ChatDto createFromParcel(Parcel source) {
            return new ChatDto(source);
        }

        @Override
        public ChatDto[] newArray(int size) {
            return new ChatDto[size];
        }
    };
}
