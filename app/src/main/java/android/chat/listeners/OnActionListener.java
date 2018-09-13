package android.chat.listeners;

import android.chat.room.entity.MessageModel;
import android.os.Message;

public interface OnActionListener {
    void onApproveClick(MessageModel messageModel);
    void onDownloadClick(MessageModel messageModel);
}
