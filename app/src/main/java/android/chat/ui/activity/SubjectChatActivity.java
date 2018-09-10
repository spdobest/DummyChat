package android.chat.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.chat.R;
import android.chat.adapter.ChatAdapter;
import android.chat.data.DatabaseHelper;
import android.chat.data.PreferenceManager;
import android.chat.model.chat.ModelChat;
import android.chat.ui.base.BaseActivity;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class SubjectChatActivity extends BaseActivity implements ChildEventListener, View.OnClickListener,
        CommonUtils.SnackbarCallback{


    private static final String TAG = "SubjectChatActivity";

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

    EmojIconActions emojIcon;


    // for image upload
    private final static int      SELECT_PICTURE                         = 1;
    ///////////////upload images/////////////
    private static final int      REQUEST_CODE_PICK_IMAGE                = 1;
    private static final int      PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 2;
    private final Activity current                                = this;
    List<ModelChat> listModelChat = new ArrayList<>();
    String            time          = "12:34 PM";
    String            image         = "";
    String            video         = "";
    String            file          = "";
    String            message;
    BottomSheetDialog bottomSheetDialog;
    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseChatReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReferenceImages;
    private DatabaseHelper databaseHelper;

    // other class declaration
    private ChatAdapter chatAdapter;
    private LinearLayoutManager linearLayoutManager;

    private ProgressDialog mProgressDialog;
    private Uri mUri;

    // variables
    private String              strName;
    private String              userId;
    private String subJectName;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat1 );

        strName = PreferenceManager.getInstance( this ).getUserName();
        userId = PreferenceManager.getInstance( this ).getUserId();

        // database initiallization
        databaseHelper = new DatabaseHelper(SubjectChatActivity.this);

        // Create an instance of Firebase Storage
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReferenceImages = mFirebaseStorage.getReferenceFromUrl( Constants.FirebaseConstants.FIREBASE_IMAGESTORAGE );

//		mStorageReferenceImages = mStorageReference.child("images");
//		FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Log.i( TAG, "onCreate: " + strName + "  " + userId );
        //	FirebaseDatabase.getInstance().setPersistenceEnabled( true );
        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference( Constants.FirebaseConstants.TABLE_ANDROID );
        databaseChatReference = FirebaseDatabase.getInstance().getReference();

        //	sendNewChat(userId,strName,"Welcome to sp chat",image,video,file,time,chatType,true);

        initializeView();

        linearLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
        linearLayoutManager.setStackFromEnd( true );
        recyclerViewChat.setLayoutManager( linearLayoutManager );

        if ( PreferenceManager.getInstance( this ).getIsChatExist() ) {
            chatAdapter = new ChatAdapter( this, listModelChat, userId );
            recyclerViewChat.setAdapter( chatAdapter );
        }


        if ( CommonUtils.isInternetAvailable( this ) ) {
            getChatData();
        }
        else {
            CommonUtils.showSnackBar( this, rootLayoutChat, getResources().getString( R.string.no_internet ), "Retry", Snackbar.LENGTH_SHORT );
        }


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {
                // Get Post object and use the values to update the UI
                Log.i( TAG, "onDataChange: " );
                for ( DataSnapshot innerDataSanpShot : dataSnapshot.getChildren() ) {
                    //DataSnapshot of inner Childerns
                    ModelChat modelChat = innerDataSanpShot.getValue( ModelChat.class );
                    Log.i( TAG, "onDataChange: 1 " + modelChat.userName + " \n " + modelChat.message );
                    if ( !PreferenceManager.getInstance( SubjectChatActivity.this ).getIsChatExist() ) {
                        listModelChat.add( modelChat );
                        databaseHelper.insertChat( SubjectChatActivity.this, modelChat, PreferenceManager.getInstance( SubjectChatActivity.this ).getIsChatExist() );
                    }
                }
                chatAdapter.notifyDataSetChanged();
                recyclerViewChat.smoothScrollToPosition( chatAdapter.getItemCount() );
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
                // Getting Post failed, log a message
                Log.w( TAG, "loadPost:onCancelled", databaseError.toException() );
            }
        };
        databaseReference.addValueEventListener( postListener );
    }


    @Override
    public void initializeView() {

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        imageViewEmoji= findViewById(R.id.imageViewEmoji);
        imageViewSend= findViewById(R.id.imageViewSend);
        imageViewAttach= findViewById(R.id.imageViewAttach);
        emojiEditText= findViewById(R.id.emojiEditText);

        toolbar= findViewById(R.id.toolbar);
        imageViewToolbarBack= findViewById(R.id.imageViewToolbarBack);
        textViewToolbarTitle= findViewById(R.id.textViewToolbarTitle);
        rootLayoutChat= findViewById(R.id.rootLayoutChat);
        root= findViewById(R.id.root);


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
                if(b){
                    Log.e(TAG, "Keyboard focussed");
                    emojIcon.closeEmojIcon();
                }
                else{

                }
            }
        });

        emojIcon.addEmojiconEditTextList(emojiEditText);


        setOnCLickListener();


        recyclerViewChat.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState ) {
                super.onScrollStateChanged( recyclerView, newState );
                if ( isRecyclerAtTop() ) {
                    //buttonLoadMore.setVisibility( View.VISIBLE );
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy ) {
                super.onScrolled( recyclerView, dx, dy );
            }
        } );

    }

    @Override
    public void setOnCLickListener() {
        imageViewSend.setOnClickListener( this );
        imageViewEmoji.setOnClickListener( this );
        imageViewAttach.setOnClickListener( this );
        recyclerViewChat.setOnClickListener( this );
        imageViewToolbarBack.setOnClickListener( this );
    }



    @Override
    public void onSnackbarActionClick() {

    }

    @Override
    public void onClick(View view) {

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
        if ( recyclerViewChat.getChildCount() == 0 ) {
            return true;
        }
        return recyclerViewChat.getChildAt( 0 ).getTop() == 0;
    }

    private void getChatData() {
//		listModelChat.clear();
        //	databaseChatReference = FirebaseDatabase.getInstance().getReference().child( Constants.FirebaseConstants.TABLE_CHAT );
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        Log.i( TAG, "onDataChange: " );
                        if ( dataSnapshot != null ) {

                            for ( DataSnapshot innerDataSanpShot : dataSnapshot.getChildren() ) {
                                //DataSnapshot of inner Childerns
                                ModelChat modelChat = innerDataSanpShot.getValue( ModelChat.class );
                                Log.i( TAG, "onDataChange: 1 " + modelChat.userName + " \n " + modelChat.message );
                                listModelChat.add( modelChat );
                            }


                            Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator             = dataSnapshotIterable.iterator();

                            while ( iterator.hasNext() ) {
                                Log.i( TAG, "onDataChange: 2 " );
                                ModelChat modelChat = iterator.next().getValue( ModelChat.class );
                                listModelChat.add( modelChat );
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {
                        //handle databaseError
                    }
                } );
        chatAdapter = new ChatAdapter( this, listModelChat, userId );
        recyclerViewChat.setAdapter( chatAdapter );
    }

}
