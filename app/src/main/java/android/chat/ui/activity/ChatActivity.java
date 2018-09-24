package android.chat.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.chat.R;
import android.chat.SpalshActivity;
import android.chat.adapter.ChatAdapter;
import android.chat.adapter.GroupChatAdapter;
import android.chat.application.ChatApplication;
import android.chat.data.PreferenceManager;
import android.chat.listeners.OnActionListener;
import android.chat.room.AppDatabase;
import android.chat.room.entity.MessageModel;
import android.chat.ui.base.BaseActivity;
import android.chat.util.ApplicationUtils;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 * Created by sibaprasad on 23/12/16.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener,
        CommonUtils.SnackbarCallback,OnActionListener {

    public static final String TAG = "ChatActivity";
    // for image upload
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 2;
    private final Activity current = this;
    List<MessageModel> listMessageModel = new ArrayList<>();
    String time = "12:34 PM";
    String image = "";
    String video = "";
    String file = "";
    String message;
    BottomSheetDialog bottomSheetDialog;
    EmojIconActions emojIcon;
    /**
     * DATABASE
     */

    AppDatabase appDatabase;
    // other class declaration
    private ChatAdapter chatAdapter;
    private ProgressDialog mProgressDialog;
    private Uri mUri;
    /**
     * WIDGET DECLARATION
     */
    private RecyclerView recyclerViewChat;
    private AppCompatImageView imageViewEmoji;
    private AppCompatImageView imageViewSend;
    private AppCompatImageView imageViewAttach;
    private EmojiconEditText emojiEditText;
    private Toolbar toolbar;
    private AppCompatImageView imageViewToolbarBack;
    private AppCompatTextView textViewToolbarTitle;
    private ConstraintLayout rootLayoutChat;
    private View root;
    private ProgressBar progressLoading;
    private LinearLayoutManager linearLayoutManager;
    /**
     * FILE UPLOAD
     */
    private Uri uriUploadedFile;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    /**
     * FIREBASE
     */
    private DatabaseReference databaseChatReference;
    private DatabaseReference databaseReferenceChat;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferenceImages;
    private StorageReference uploadFileReference;
    /**
     * END FIREBASE
     */

    // variables
    private String senderName;
    private String currentUserId;
    private String recieverId;
    private String recieverName;
    private String senderRecieverId;
    private String senderRecieverIdRev;
    private MessageModel messageModelFromNotification;

    private List<MessageModel> listMessageModelToDelete = new ArrayList<>();

    /**
     * MEDIA PLAYER TO PLAY MUSIC
     */
    MediaPlayer mediaPlayerSent;
    MediaPlayer mediaPlayerRecieve;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);




        mediaPlayerSent = MediaPlayer.create(getApplicationContext(),R.raw.sent);//Create MediaPlayer object with MP3 file under res/raw folder
        mediaPlayerRecieve = MediaPlayer.create(getApplicationContext(),R.raw.recieve);//Create MediaPlayer object with MP3 file under res/raw folder

        appDatabase = AppDatabase.getAppDatabase(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FirebaseConstants.DATABASE_PATH_UPLOADS);

        databaseReferenceChat = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_CHAT);
        databaseChatReference = FirebaseDatabase.getInstance().getReference();

        // Create an instance of Firebase Storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReferenceImages = mFirebaseStorage.getReferenceFromUrl(Constants.FirebaseConstants.FIREBASE_IMAGESTORAGE);


        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Constants.BundleKeys.RECIEVER_ID)) {
                recieverId = intent.getExtras().getString(Constants.BundleKeys.RECIEVER_ID);
            }

            if (intent.hasExtra(Constants.BundleKeys.RECIEVER_NAME)) {
                recieverName = intent.getExtras().getString(Constants.BundleKeys.RECIEVER_NAME);
            }
            if (intent.hasExtra(Constants.BundleKeys.SENDER_RECIEVER_ID)) {
                senderRecieverId = intent.getExtras().getString(Constants.BundleKeys.SENDER_RECIEVER_ID);
            }
        }

        String notId = PreferenceManager.getInstance(this).getNotificationId();
        if(!TextUtils.isEmpty(notId)){
            recieverId = notId;
        }

        senderName = PreferenceManager.getInstance(this).getUserName();
        currentUserId = PreferenceManager.getInstance(this).getUserId();


        initializeView();


        textViewToolbarTitle.setText(recieverName);

        senderRecieverId = currentUserId+"-"+recieverId;
        senderRecieverIdRev = recieverId+"-"+currentUserId;

        if (CommonUtils.isInternetAvailable(this)) {

        } else {
            CommonUtils.showSnackBar(this, rootLayoutChat, getResources().getString(R.string.no_internet), "Retry", Snackbar.LENGTH_SHORT);
        }
        getChatDataFromDatabase();

        HomeActivity.cancelJob(ChatActivity.this);

        notifyChatDatachangedInServer();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (intent.hasExtra(Constants.BundleKeys.RECIEVER_ID)) {
                recieverId = intent.getExtras().getString(Constants.BundleKeys.RECIEVER_ID);
            }

            if (intent.hasExtra(Constants.BundleKeys.RECIEVER_NAME)) {
                recieverName = intent.getExtras().getString(Constants.BundleKeys.RECIEVER_NAME);
            }
            if (intent.hasExtra(Constants.BundleKeys.SENDER_RECIEVER_ID)) {
                senderRecieverId = intent.getExtras().getString(Constants.BundleKeys.SENDER_RECIEVER_ID);
            }

        }

        senderName = PreferenceManager.getInstance(this).getUserName();
        currentUserId = PreferenceManager.getInstance(this).getUserId();
    }

    @Override
    public void initializeView() {

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        imageViewEmoji = findViewById(R.id.imageViewEmoji);
        imageViewSend = findViewById(R.id.imageViewSend);
        imageViewAttach = findViewById(R.id.imageViewAttach);
        emojiEditText = findViewById(R.id.emojiEditText);

        toolbar = findViewById(R.id.toolbar);

        toolbar.inflateMenu(R.menu.menu_chat);

        imageViewToolbarBack = findViewById(R.id.imageViewToolbarBack);
        textViewToolbarTitle = findViewById(R.id.textViewToolbarTitle);
        rootLayoutChat = findViewById(R.id.rootLayoutChat);
        root = findViewById(R.id.root);
        progressLoading = findViewById(R.id.progressLoading);


        emojIcon = new EmojIconActions(this, root, emojiEditText, imageViewEmoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
                emojIcon.ShowEmojIcon();
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
                emojIcon.closeEmojIcon();
            }
        });

        emojiEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Log.e(TAG, "Keyboard focussed");
                    emojIcon.closeEmojIcon();
                } else {

                }
            }
        });

        emojIcon.addEmojiconEditTextList(emojiEditText);


        setOnCLickListener();


        recyclerViewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isRecyclerAtTop()) {
                    //buttonLoadMore.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        /**
         * SET UP RECYCLERVIEW
         */
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        chatAdapter = new ChatAdapter(this, listMessageModel, currentUserId,this);
        recyclerViewChat.setAdapter(chatAdapter);

    }

    @Override
    public void setOnCLickListener() {
        imageViewSend.setOnClickListener(this);
        imageViewEmoji.setOnClickListener(this);
        imageViewAttach.setOnClickListener(this);
        recyclerViewChat.setOnClickListener(this);
        imageViewToolbarBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewSend:
                message = emojiEditText.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    CommonUtils.showSnackBar(rootLayoutChat, "Please Enter Message", Snackbar.LENGTH_SHORT);
                } else {
                    sendChatDataToFirebase(message, ChatAdapter.ROW_TYPE_TEXT);
                }
                emojiEditText.setText("");

                break;
            case R.id.imageViewEmoji:
                emojIcon.ShowEmojIcon();
                break;
            case R.id.imageViewAttach:
                showSubjectBottomsheet();
                break;
            case R.id.imageViewToolbarBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onSnackbarActionClick() {
        if (CommonUtils.isInternetAvailable(this)) {

        } else {
            CommonUtils.showSnackBar(this, rootLayoutChat, getResources().getString(R.string.no_internet), "Retry", Snackbar.LENGTH_SHORT);
        }
    }

    private void getChatDataFromDatabase() {
        List<MessageModel> list0 = appDatabase.getMessageDao().getAll();
        List<MessageModel> list = appDatabase.getMessageDao().getMessageByOneId(senderRecieverId, senderRecieverIdRev);


        if (list != null && list.size() > 0) {
            refreshChatList(list);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//				pickImage();
                //		ImageVideoDialogFragment.newInstance( true, selected_filetype, "" ).show( getSupportFragmentManager(), ImageVideoDialogFragment.TAG );
            }
        }
    }

    private void hideProgressDialog1() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void updateProgress(int progress) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(progress);
            if (progress == 100) {
                hideProgressDialog1();
                Toast.makeText(ChatActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private boolean isRecyclerAtTop() {
        if (recyclerViewChat.getChildCount() == 0) {
            return true;
        }
        return recyclerViewChat.getChildAt(0).getTop() == 0;
    }

    public void showSubjectBottomsheet() {
        RecyclerView recyclerView;

        AppCompatImageView imageViewAttachImageChat;
        AppCompatImageView imageViewAttachLocChat;
        AppCompatImageView imageViewAttachDocChat;
        AppCompatImageView imageViewAttachVideoChat;

        if (bottomSheetDialog == null)
            bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);

        bottomSheetDialog.setContentView(R.layout.bottomsheet_attach);

        imageViewAttachImageChat = bottomSheetDialog.findViewById(R.id.imageViewAttachImageChat);
        imageViewAttachVideoChat = bottomSheetDialog.findViewById(R.id.imageViewAttachVideoChat);
        imageViewAttachDocChat = bottomSheetDialog.findViewById(R.id.imageViewAttachDocChat);
        imageViewAttachLocChat = bottomSheetDialog.findViewById(R.id.imageViewAttachLocChat);

        imageViewAttachImageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imageViewAttachVideoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imageViewAttachDocChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPDF();
            }
        });

        imageViewAttachLocChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bottomSheetDialog.show();

    }

    private void sendChatDataToFirebase(String message, int messageType) {
        /**
         * String chatKey, String senderName, String currentUserId,
         String message, String messageTimeInMillis, String messageDate,
         String messageTime, int messageType, String pathOrUrl, int isDownloaded,
         int isSent, String groupName, int isTeacher, int isCurrentUser,int isAccepted
         */

        MessageModel messageModel = new MessageModel(
                "", currentUserId, senderName, recieverId,senderRecieverId, message, "" + System.currentTimeMillis(),
                CommonUtils.getCurrentDate(), CommonUtils.getCurrentTime(),
                messageType, "", 0, 0, "",
                PreferenceManager.getInstance(this).getIsStudent() ? 0 : 1,
                0
        );


        if (CommonUtils.isInternetAvailable(this)) {
            String key = databaseChatReference.getKey();
            String insertKey = databaseChatReference.push().getKey();
            messageModel.setChatKey(insertKey);
            try {
                databaseChatReference.child(Constants.FirebaseConstants.TABLE_CHAT).child(insertKey).setValue(messageModel);
            mediaPlayerSent.start();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, e.getMessage());
            }
        }
        insertChatToDB(messageModel);

        refreshChatList(messageModel);
    }


    private void refreshChatList(List<MessageModel> listMessage) {
        if (listMessage != null && listMessage.size() > 0) {
            int size = listMessageModel.size();
            listMessageModel.addAll(listMessage);
            chatAdapter.notifyDataSetChanged();
            //	chatAdapter.updateMessageList(listMessageModel);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewChat.smoothScrollToPosition(listMessageModel.size()-1);
                }
            }, 200);
        }
    }

    private void refreshChatList(MessageModel messageModel) {
        if (messageModel != null) {
            int size = listMessageModel.size();
            listMessageModel.add(messageModel);
            //	chatAdapter.updateMessageList(listMessageModel);

            final int newMsgPosition = listMessageModel.size() - 1;
            // Notify recycler view insert one new data.
            chatAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            recyclerViewChat.scrollToPosition(newMsgPosition);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerViewChat.smoothScrollToPosition(newMsgPosition);
                }
            }, 200);

        }
    }

    private void showProgress(boolean isShow) {
        if (isShow) {
            progressLoading.setVisibility(View.VISIBLE);
            imageViewSend.setClickable(false);
        } else {
            progressLoading.setVisibility(View.GONE);
            imageViewSend.setClickable(true);
        }
    }

    private void getPDF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constants.PICK_PDF_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                uriUploadedFile = data.getData();
                uploadFileDialog();
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("File Upload");
        builder.setMessage("Are you sure you want to Upload File?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                uploadFile(uriUploadedFile);
                showProgressDialog("Please Wait");
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
                bottomSheetDialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void uploadFile(Uri data) {

        UploadTask uploadTask = uploadFileReference.putFile(data);

        showProgress(true);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return uploadFileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    showProgress(true);
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    private void insertChatToDB(MessageModel messageModel) {

        String lastMessageDate = appDatabase.getMessageDao().getLastMessageDate();

        if (CommonUtils.getCurrentDate().equalsIgnoreCase(lastMessageDate) && !TextUtils.isEmpty(lastMessageDate)) {
            appDatabase.getMessageDao().insertMessage(messageModel);
        } else {
            MessageModel messageModel1 = new MessageModel();
            messageModel1.setMessageDate(CommonUtils.getCurrentDate());
            messageModel1.setMessageTime(CommonUtils.getCurrentDate());
            messageModel1.setSenderRecieverId(senderRecieverId);
            messageModel1.setMessageTimeInMillis("" + System.currentTimeMillis());
            messageModel1.setMessageType(GroupChatAdapter.ROW_TYPE_DATE);
            appDatabase.getMessageDao().insertMessage(messageModel1);
        }
    }

    private void notifyChatDatachangedInServer() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);


                if (messageModel != null && TextUtils.isEmpty(messageModel.getGroupName())
                     &&!TextUtils.isEmpty(messageModel.getSenderId())   && messageModel.getSenderId().equalsIgnoreCase(currentUserId)
                        ) {
                    if(TextUtils.isEmpty(appDatabase.getMessageDao().getChatKey(messageModel.getChatKey()))){

                        if(messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverId) ||
                                messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverIdRev)
                                ) {
                            long returnId = appDatabase.getMessageDao().insertMessage(messageModel);

                            refreshChatList(messageModel);
                            mediaPlayerRecieve.start();
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
                Toast.makeText(ChatActivity.this, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceChat.addChildEventListener(childEventListener);
    }

   /* private void getDatafromNotification(MessageModel messageModel){
        if (messageModel != null && TextUtils.isEmpty(messageModel.getGroupName())
                && messageModel.getSenderId().equalsIgnoreCase(currentUserId)
                ) {
            if(TextUtils.isEmpty(appDatabase.getMessageDao().getChatKey(messageModel.getChatKey()))){

                if(messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverId) ||
                        messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverIdRev)
                        ) {
                    long returnId = appDatabase.getMessageDao().insertMessage(messageModel);
                }
            }
            getChatDataFromDatabase();
        }
    }*/

    private void excx(){
        Query chatDetailsQuery = ChatApplication.getFirebaseDatabaseReference().
                child(Constants.FirebaseConstants.TABLE_CHAT).orderByChild("currentUserId").equalTo(recieverId);

        chatDetailsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: ");
                if (dataSnapshot != null) {

                    Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = dataSnapshotIterable.iterator();

                    while (iterator.hasNext()) {
                        Log.i(TAG, "onDataChange: 2 ");
                        MessageModel messageModel = iterator.next().getValue(MessageModel.class);
                        if (messageModel != null && TextUtils.isEmpty(messageModel.getGroupName())
                                && messageModel.getSenderId().equalsIgnoreCase(currentUserId)
                                ) {
                            if(TextUtils.isEmpty(appDatabase.getMessageDao().getChatKey(messageModel.getChatKey()))){

                                if(messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverId) ||
                                        messageModel.getSenderRecieverId().equalsIgnoreCase(senderRecieverIdRev)
                                        ) {
                                    long returnId = appDatabase.getMessageDao().insertMessage(messageModel);

                                    refreshChatList(messageModel);
                                    mediaPlayerRecieve.start();
                                }
                            }
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_chat, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_share:

                return true;
            case R.id.menu_delete:
                if(listMessageModelToDelete.size()>0) {
                    for (MessageModel messageModel : listMessageModelToDelete) {
                        appDatabase.getMessageDao().delete(messageModel);
                        listMessageModel.remove(messageModel);
                    }
                    refreshChatList(listMessageModel);
                }
                break;
        }
        return super.onOptionsItemSelected( item );
    }


    @Override
    public void onApproveClick(MessageModel messageModel) {

    }

    @Override
    public void onDownloadClick(MessageModel messageModel) {

    }

    @Override
    public void onLongPressSelect(MessageModel messageModel) {
        if(messageModel!=null){
            if(messageModel.isSelected){
            listMessageModelToDelete.add(messageModel);
            }
            else{
                listMessageModelToDelete.remove(messageModel);
            }
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        HomeActivity.scheduleJob(getApplicationContext());
    }
}
