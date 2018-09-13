package android.chat.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.chat.R;
import android.chat.adapter.GroupChatAdapter;
import android.chat.application.ChatApplication;
import android.chat.data.PreferenceManager;
import android.chat.listeners.OnActionListener;
import android.chat.room.AppDatabase;
import android.chat.room.entity.MessageModel;
import android.chat.ui.base.BaseActivity;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Iterator;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class SubjectChatActivity extends BaseActivity implements ChildEventListener, View.OnClickListener,
        CommonUtils.SnackbarCallback, OnActionListener {


    private static final String TAG = "SubjectChatActivity";
    // for image upload
    private final static int SELECT_PICTURE = 1;
    ///////////////upload images/////////////
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 2;
    private final Activity current = this;
    EmojIconActions emojIcon;
    List<MessageModel> listModelChat = new ArrayList<>();
    String time = "12:34 PM";
    String image = "";
    String video = "";
    String file = "";
    String message;
    BottomSheetDialog bottomSheetDialog;
    StorageReference uploadFileReference;
    /**
     * DATABASE
     */

    AppDatabase appDatabase;
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
    /**
     * FILE UPLOAD
     */
    private Uri uriUploadedFile;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseChatReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferenceImages;
    // other class declaration
    private GroupChatAdapter groupChatAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ProgressDialog mProgressDialog;
    private Uri mUri;
    // variables
    private String senderName;
    private String senderId;
    private String recieverId;
    private String recieverName;
    private String groupName;

    private String pdfFilePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);

        initializeView();

        appDatabase = AppDatabase.getAppDatabase(this);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FirebaseConstants.DATABASE_PATH_UPLOADS);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_ANDROID);
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

            if (intent.hasExtra(Constants.BundleKeys.GROUP_NAME)) {
                groupName = intent.getExtras().getString(Constants.BundleKeys.GROUP_NAME);
            }
        }


        senderName = PreferenceManager.getInstance(this).getUserName();
        senderId = PreferenceManager.getInstance(this).getUserId();


        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(linearLayoutManager);

        groupChatAdapter = new GroupChatAdapter(SubjectChatActivity.this, listModelChat, senderId, this);
        recyclerViewChat.setAdapter(groupChatAdapter);


        if (CommonUtils.isInternetAvailable(this)) {
            getChatData();
        } else {
            CommonUtils.showSnackBar(this, rootLayoutChat, getResources().getString(R.string.no_internet), "Retry", Snackbar.LENGTH_SHORT);
        }


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.i(TAG, "onDataChange: ");
                for (DataSnapshot innerDataSanpShot : dataSnapshot.getChildren()) {
                    //DataSnapshot of inner Childerns
                    MessageModel modelChat = innerDataSanpShot.getValue(MessageModel.class);
                    Log.i(TAG, "onDataChange: 1 " + modelChat.getSenderName() + " \n " + modelChat.getMessage());
                    /*if (!PreferenceManager.getInstance(SubjectChatActivity.this).getIsChatExist()) {
                        listModelChat.add(modelChat);
                        // databaseHelper.insertChat( SubjectChatActivity.this, modelChat, PreferenceManager.getInstance( SubjectChatActivity.this ).getIsChatExist() );
                    }*/
                }
                /*groupChatAdapter.notifyDataSetChanged();
                recyclerViewChat.smoothScrollToPosition(groupChatAdapter.getItemCount());*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);
    }


    @Override
    public void initializeView() {

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        imageViewEmoji = findViewById(R.id.imageViewEmoji);
        imageViewSend = findViewById(R.id.imageViewSend);
        imageViewAttach = findViewById(R.id.imageViewAttach);
        emojiEditText = findViewById(R.id.emojiEditText);

        toolbar = findViewById(R.id.toolbar);
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
    public void onSnackbarActionClick() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewAttach:
                showSubjectBottomsheet();
                break;
            case R.id.imageViewSend:
                String message = emojiEditText.getText().toString().trim();

                if (!CommonUtils.isInternetAvailable(this)) {
                    Toast.makeText(SubjectChatActivity.this, "Internet Not Available", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(message)) {
                    Toast.makeText(SubjectChatActivity.this, "Message Cannot be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    sendNewChat(message, GroupChatAdapter.ROW_TYPE_TEXT);
                }

                emojiEditText.setText("");

                break;
            case R.id.imageViewEmoji:
                emojIcon.ShowEmojIcon();
                break;
            case R.id.imageViewToolbarBack :
                onBackPressed();
                break;
        }

    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

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
            bottomSheetDialog = new BottomSheetDialog(SubjectChatActivity.this);

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
        uploadFileReference = mStorageReference.child(Constants.FirebaseConstants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
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


                    pdfFilePath =   "https://firebasestorage.googleapis.com/v0/b/my-chat-f5ef1.appspot.com/o/uploads%2F1536827908804.pdf?alt=media&token=01dbf408-d476-4383-bcdc-28b47c60ddc8";

                    showProgress(false);
                    Uri downloadUri = task.getResult();
                    pdfFilePath = downloadUri.toString();
                    sendNewChat("",GroupChatAdapter.ROW_TYPE_FILE);
                    Log.i(TAG, "onComplete: "+downloadUri.getPath());


                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    private void sendNewChat(String message, int messageType) {
        /**
         * String chatKey,String currentUserId ,String senderName, String senderId,
         String message, String messageTimeInMillis, String messageDate,
         String messageTime, int messageType, String pathOrUrl, int isDownloaded,
         int isSent, String groupName, int isTeacher,int isAccepted
         */

        MessageModel MessageModel = new MessageModel(
                "",senderId, senderName, senderId, message, "" + System.currentTimeMillis(),
                CommonUtils.getCurrentDate(), CommonUtils.getCurrentTime(),
                messageType, pdfFilePath, 0, 0, groupName,
                PreferenceManager.getInstance(this).getIsStudent() ? 0 : 1,
                0
        );

        if (MessageModel != null) {
            String key = databaseChatReference.getKey();
            String insertKey = databaseChatReference.push().getKey();
            // pushing user to 'users' node using the userId
//		databaseChatReference.child(userId).setValue(modelChat);

            MessageModel.setChatKey(insertKey);

            try {

                appDatabase = AppDatabase.getAppDatabase(this);
                databaseChatReference.child(Constants.FirebaseConstants.TABLE_CHAT).child(insertKey).setValue(MessageModel);

                appDatabase.getMessageDao().insertMessage(MessageModel);

            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, e.getMessage());
            }
        }

        refreshChatList(MessageModel);

    }


    private void refreshChatList(List<MessageModel> listMessage) {
        if (listMessage != null && listMessage.size() > 0) {
            int size = listModelChat.size();
            listModelChat.addAll(listMessage);
            groupChatAdapter.notifyDataSetChanged();
            recyclerViewChat.scrollToPosition(listModelChat.size()-1);
        }
    }

    private void refreshChatList(MessageModel messageModel) {
        if (messageModel != null) {
            int size = listModelChat.size();
            listModelChat.add(messageModel);
            groupChatAdapter.notifyDataSetChanged();
            recyclerViewChat.scrollToPosition(listModelChat.size()-1);
        }
    }

    private void getAllGroupData(String subjectName) {
        List<MessageModel> list = appDatabase.getMessageDao().getChatdataBySubject1(subjectName);
        List<MessageModel> list1 = appDatabase.getMessageDao().getAllChat();

        if (list != null && list.size() > 0) {
            listModelChat.addAll(list);
            refreshChatList(list);
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

    private void getChatData() {

        Query chatDetailsQuery = ChatApplication.getFirebaseDatabaseReference().
                child(Constants.FirebaseConstants.TABLE_CHAT).orderByChild("groupName").equalTo(groupName);

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
                        listModelChat.add(messageModel);
                    }
                    groupChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                showSnackbar("Error Fetching Data ", Snackbar.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onApproveClick(MessageModel messageModel) {
        if (!PreferenceManager.getInstance(this).getIsStudent()) {
            showProgress(true);
            if (messageModel != null) {
                String chatKey = messageModel.getChatKey();
                messageModel.setIsAccepted(1);
                ChatApplication.getFirebaseDatabaseReference().child(Constants.FirebaseConstants.TABLE_CHAT).child(chatKey)
                        .setValue(messageModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showProgress(false);
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "onComplete: success ");
                                    showSnackbar("Updated Successfully", Snackbar.LENGTH_SHORT);
                                } else {
                                    Log.i(TAG, "onComplete: fail");
                                    showSnackbar("Updated Successfully", Snackbar.LENGTH_LONG);
                                    showProgress(false);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onDownloadClick(MessageModel messageModel) {
        if(messageModel!=null){
            Uri uri = Uri.parse(messageModel.getPathOrUrl());
        }
    }
}
