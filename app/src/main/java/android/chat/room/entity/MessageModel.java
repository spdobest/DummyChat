package android.chat.room.entity;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "messageTable")
public class MessageModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ColumnInfo(name = "chatKey")
    public String chatKey;

    @ColumnInfo(name = "currentUserId")
    public String currentUserId;

    @ColumnInfo(name = "senderName")
    public String senderName;

    @ColumnInfo(name = "senderId")
    public String senderId;


    @ColumnInfo(name = "message")
    public String message;


    @ColumnInfo(name = "messageTimeInMillis")
    public String messageTimeInMillis;

    @ColumnInfo(name = "messageDate")
    public String messageDate;

    @ColumnInfo(name = "messageTime")
    public String messageTime;


    @ColumnInfo(name = "messageType")
    public int messageType;

    @ColumnInfo(name = "PathOrUrl")
    public String pathOrUrl;

    @ColumnInfo(name = "isDownloaded")
    public int isDownloaded;

    @ColumnInfo(name = "isSent")
    public int isSent;

    @ColumnInfo(name = "groupName")
    public String groupName;

    @ColumnInfo(name = "isTeacher")
    public int isTeacher;


    @Ignore
    public String getChatKey() {
        return chatKey;
    }
    @Ignore
    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }
    @Ignore
    public String getSenderName() {
        return senderName;
    }
    @Ignore
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    @Ignore
    public String getSenderId() {
        return senderId;
    }
    @Ignore
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    @Ignore
    public String getMessage() {
        return message;
    }
    @Ignore
    public void setMessage(String message) {
        this.message = message;
    }
    @Ignore
    public String getMessageTimeInMillis() {
        return messageTimeInMillis;
    }
    @Ignore
    public void setMessageTimeInMillis(String messageTimeInMillis) {
        this.messageTimeInMillis = messageTimeInMillis;
    }
    @Ignore
    public String getMessageDate() {
        return messageDate;
    }
    @Ignore
    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }
    @Ignore
    public String getMessageTime() {
        return messageTime;
    }
    @Ignore
    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
    @Ignore
    public int getMessageType() {
        return messageType;
    }
    @Ignore
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    @Ignore
    public String getPathOrUrl() {
        return this.pathOrUrl;
    }
    @Ignore
    public void setPathOrUrl(String pathOrUrl) {
        this.pathOrUrl = pathOrUrl;
    }
    @Ignore
    public int getIsDownloaded() {
        return isDownloaded;
    }
    @Ignore
    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
    @Ignore
    public int getIsSent() {
        return isSent;
    }
    @Ignore
    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }
    @Ignore
    public String getGroupName() {
        return groupName;
    }
    @Ignore
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    @Ignore
    public int getIsTeacher() {
        return isTeacher;
    }
    @Ignore
    public void setIsTeacher(int isTeacher) {
        this.isTeacher = isTeacher;
    }

    @Ignore
    public int getIsAccepted() {
        return isAccepted;
    }
    @Ignore
    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }

    @ColumnInfo(name = "isAccepted")
    public int isAccepted;



    public MessageModel() {
    }

    @Ignore
    public String getCurrentUserId() {
        return currentUserId;
    }
    @Ignore
    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Ignore
    public MessageModel(String chatKey,String currentUserId ,String senderName, String senderId,
                        String message, String messageTimeInMillis, String messageDate,
                        String messageTime, int messageType, String pathOrUrl, int isDownloaded,
                        int isSent, String groupName, int isTeacher,int isAccepted) {
        this.chatKey = chatKey;
        this.currentUserId = currentUserId;
        this.senderName = senderName;
        this.senderId = senderId;
        this.message = message;
        this.messageTimeInMillis = messageTimeInMillis;
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.messageType = messageType;
        this.pathOrUrl = pathOrUrl;
        this.isDownloaded = isDownloaded;

        this.isSent = isSent;
        this.groupName = groupName;
        this.isTeacher = isTeacher;
        this.isAccepted = isAccepted;
    }

}
