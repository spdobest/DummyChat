package android.chat.background;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.chat.R;
import android.chat.data.PreferenceManager;
import android.chat.room.AppDatabase;
import android.chat.room.entity.MessageModel;
import android.chat.ui.activity.ChatActivity;
import android.chat.util.Constants;
import android.chat.util.NotificationHelper;
import android.chat.util.NotificationUtil;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageReadingService extends IntentService {

    private static final String TAG = "MessageReadingService";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceChat;
    /**
     * DATABASE
     */
    private AppDatabase appDatabase;
    private String currentUserId;
    private NotificationUtil notificationUtil;
    private  NotificationHelper  notificationHelper;
    private String senderRecieverId;
    private String senderRecieverIdRev;

    public MessageReadingService() {
        super("MessageReadingService");
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /*@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        MyBinder binder = new MyBinder();
        return binder;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceChat = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_CHAT);
        appDatabase = AppDatabase.getAppDatabase(getApplicationContext());
        currentUserId = PreferenceManager.getInstance(getApplicationContext()).getUserId();
        notificationUtil = new NotificationUtil();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHelper = new NotificationHelper(getApplicationContext());
        }

        readFirebaseServer();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

        }
    }

    public void readFirebaseServer() {
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

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                    postOreoNotification(123,messageModel);
                                    PreferenceManager.getInstance(getApplicationContext()).setNotificationId(messageModel.getCurrentUserId());
                                }
                                else{
                                    long returnId = appDatabase.getMessageDao().insertMessage(messageModel);
                                    notificationUtil.showStandardHeadsUpNotification(getApplicationContext(),messageModel,senderRecieverId);

                                    }
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
                Toast.makeText(getApplicationContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceChat.addChildEventListener(childEventListener);
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);




    }

    public class MyBinder extends Binder {
        public MessageReadingService getService() {
            return MessageReadingService.this;
        }
    }


    public void postOreoNotification(int id, MessageModel messageModel) {

        Notification.Builder notificationBuilder = notificationHelper.getNotification1(messageModel.getSenderName(),
                messageModel.getMessage());

        if (notificationBuilder != null) {

            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);



            intent.putExtra(Constants.BundleKeys.RECIEVER_ID,messageModel.getCurrentUserId());
            intent.putExtra(Constants.BundleKeys.RECIEVER_NAME,messageModel.getSenderName());
            intent.putExtra(Constants.BundleKeys.SENDER_RECIEVER_ID,senderRecieverId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);


            notificationBuilder.setContentIntent(pendingIntent);

            notificationHelper.notify(id, notificationBuilder);
        }
    }


}
