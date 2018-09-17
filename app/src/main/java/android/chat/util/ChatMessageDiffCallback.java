package android.chat.util;

import android.chat.room.entity.MessageModel;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class ChatMessageDiffCallback extends DiffUtil.Callback {

    private final List<MessageModel> mOldEmployeeList;
    private final List<MessageModel> mNewEmployeeList;

    public ChatMessageDiffCallback(List<MessageModel> oldEmployeeList, List<MessageModel> newEmployeeList) {
        this.mOldEmployeeList = oldEmployeeList;
        this.mNewEmployeeList = newEmployeeList;
    }

    @Override
    public int getOldListSize() {
        return mOldEmployeeList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewEmployeeList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldEmployeeList.get(oldItemPosition).getId() == mNewEmployeeList.get(
                newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final MessageModel oldEmployee = mOldEmployeeList.get(oldItemPosition);
        final MessageModel newEmployee = mNewEmployeeList.get(newItemPosition);

        return oldEmployee.getMessage().equals(newEmployee.getMessage());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}