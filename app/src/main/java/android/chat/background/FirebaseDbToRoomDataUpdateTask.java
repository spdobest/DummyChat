package android.chat.background;

import android.chat.data.PreferenceManager;
import android.chat.room.AppDatabase;
import android.chat.room.entity.MessageModel;
import android.chat.util.Constants;
import android.chat.util.NotificationUtil;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.Executor;

public class FirebaseDbToRoomDataUpdateTask {

    private static final String TAG = "FirebaseDbToRoomDataUpd";

    private TaskExecutor taskExecutor;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceChat;
    /**
     * DATABASE
     */
    AppDatabase appDatabase;

    String currentUserId;

    NotificationUtil notificationUtil;

    String senderRecieverId;
    String senderRecieverIdRev;

    private Context context;

    public FirebaseDbToRoomDataUpdateTask(Context context){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceChat = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_CHAT);
        appDatabase = AppDatabase.getAppDatabase(context);
        currentUserId = PreferenceManager.getInstance(context).getUserId();
        notificationUtil = new NotificationUtil();
        taskExecutor = new TaskExecutor();
        this.context = context;

        taskExecutor.execute(new RoomUpdateTask());


        taskExecutor.execute(new RoomUpdateTask());


    }

    public class TaskExecutor implements Executor {
        @Override
        public void execute(@NonNull Runnable runnable) {
           Thread t =  new Thread(runnable);
           t.start();
        }
    }
    public class RoomUpdateTask implements Runnable{
        private Context context;
        public RoomUpdateTask(){
        }
        @Override
        public void run() {
            readFirebaseServer( context);
        }
    }

    public void readFirebaseServer(final Context context) {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);


                if (messageModel != null && TextUtils.isEmpty(messageModel.getGroupName())
                        && messageModel.getSenderId().equalsIgnoreCase(currentUserId)
                        ) {

                    senderRecieverId = currentUserId+"-"+messageModel.getCurrentUserId();
                    senderRecieverIdRev = messageModel.getCurrentUserId()+"-"+currentUserId;

                    if (messageModel != null && TextUtils.isEmpty(messageModel.getGroupName())
                            && messageModel.getSenderId().equalsIgnoreCase(currentUserId)
                            ) {
                        if(TextUtils.isEmpty(appDatabase.getMessageDao().getChatKey(messageModel.getChatKey()))){

                            if(messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverId) ||
                                    messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverIdRev)
                                    ) {
                                long returnId = appDatabase.getMessageDao().insertMessage(messageModel);
                                notificationUtil.showStandardHeadsUpNotification(context,messageModel,"",false);

                            }
                        }
                    }

                }


              /*  if (messageModel != null && !messageModel.getCurrentUserId().equalsIgnoreCase(currentUserId)) {
                    if(TextUtils.isEmpty(appDatabase.getMessageDao().getChatKey(messageModel.getChatKey()))) {
                        appDatabase.getMessageDao().insertMessage(messageModel);
                        refreshChatList(messageModel);
                    }
                }*/
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // A new comment has been added, add it to the displayed list
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                // A new comment has been added, add it to the displayed list
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                String commentKey = dataSnapshot.getKey();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };
        databaseReferenceChat.addChildEventListener(childEventListener);
    }

}