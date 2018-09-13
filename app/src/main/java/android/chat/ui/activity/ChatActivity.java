package android.chat.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.chat.R;
import android.chat.adapter.ChatAdapter;
import android.chat.adapter.GroupChatAdapter;
import android.chat.application.ChatApplication;
import android.chat.data.PreferenceManager;
import android.chat.model.chat.ModelChat;
import android.chat.room.AppDatabase;
import android.chat.room.entity.MessageModel;
import android.chat.ui.base.BaseActivity;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 * Created by sibaprasad on 23/12/16.
 */

public class ChatActivity extends BaseActivity implements ChildEventListener, View.OnClickListener,
		CommonUtils.SnackbarCallback {

	public static final  String   TAG                                    = "ChatActivity";
	// for image upload
	private static final int      REQUEST_CODE_PICK_IMAGE                = 1;
	private static final int      PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 2;
	private final        Activity current                                = this;
	List<MessageModel> listMessageModel = new ArrayList<>();
	String            time          = "12:34 PM";
	String            image         = "";
	String            video         = "";
	String            file          = "";
	String            message;
	BottomSheetDialog bottomSheetDialog;

	// other class declaration
	private ChatAdapter chatAdapter;

	private ProgressDialog     mProgressDialog;
	private Uri                mUri;

	/**
	 * WIDGET DECLARATION
	 */
	private RecyclerView recyclerViewChat;
	private AppCompatImageView imageViewEmoji;
	private AppCompatImageView imageViewSend;
	private AppCompatImageView imageViewAttach;
	private EmojiconEditText   emojiEditText;
	private Toolbar toolbar;
	private AppCompatImageView imageViewToolbarBack;
	private AppCompatTextView textViewToolbarTitle;
	private ConstraintLayout rootLayoutChat;
	private View root;
	private ProgressBar progressLoading;

	private LinearLayoutManager 	linearLayoutManager;


	EmojIconActions emojIcon;
	/**
	 * DATABASE
	 */

	AppDatabase appDatabase;

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
	private String senderId;
	private String recieverId;
	private String recieverName;
	private String groupName;


//	private DatabaseHelper databaseHelper;
	private int[] chatCount        = { 20, 50, 100, };
	private int   loadMorePosition = 0;

	boolean isSUbjectChat;

	public static Bitmap getBitmapFromLocalPath( String path, int sampleSize ) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			return BitmapFactory.decodeFile( path, options );
		}
		catch ( Exception e ) {
			//  Logger.e(e.toString());
		}

		return null;
	}

	public static boolean saveBitmap( Bitmap original, Bitmap.CompressFormat format, int quality, String outputLocation ) {
		if ( original == null ) {
			return false;
		}

		try {
			return original.compress( format, quality, new FileOutputStream( outputLocation ) );
		}
		catch ( Exception e ) {
			//  Logger.e(e.toString());
		}

		return false;
	}

	public static Bitmap getResizedBitmap( String filepath, int widthLimit, int heightLimit, int totalSize ) {
		int         outWidth  = 0;
		int         outHeight = 0;
		int         resize    = 1;
		InputStream input     = null;

		try {
			input = new FileInputStream( new File( filepath ) );

			BitmapFactory.Options getSizeOpt = new BitmapFactory.Options();
			getSizeOpt.inJustDecodeBounds = true;
			BitmapFactory.decodeStream( input, null, getSizeOpt );
			outWidth = getSizeOpt.outWidth;
			outHeight = getSizeOpt.outHeight;

			while ( ( outWidth / resize ) > widthLimit || ( outHeight / resize ) > heightLimit ) {
				resize *= 2;
			}
			resize = resize * ( totalSize + 15 ) / 15;

			BitmapFactory.Options resizeOpt = new BitmapFactory.Options();
			resizeOpt.inSampleSize = resize;

			input.close();
			input = null;

			input = new FileInputStream( new File( filepath ) );
			Bitmap bitmapImage = BitmapFactory.decodeStream( input, null, resizeOpt );
			return bitmapImage;
		}
		catch ( Exception e ) {
			//  Logger.e(e.toString());
		}
		finally {
			if ( input != null ) {
				try {
					input.close();
				}
				catch ( IOException e ) {
					//  Logger.e(e.toString());
				}
			}
		}
		return null;
	}

	@Override
	protected void onCreate( @Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_chat1 );


		initializeView();

		appDatabase = AppDatabase.getAppDatabase(this);
		firebaseDatabase = FirebaseDatabase.getInstance();
		mStorageReference = FirebaseStorage.getInstance().getReference();
		mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.FirebaseConstants.DATABASE_PATH_UPLOADS);

		databaseReferenceChat = firebaseDatabase.getReference( Constants.FirebaseConstants.TABLE_CHAT );
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

		senderName = PreferenceManager.getInstance( this ).getUserName();
		senderId = PreferenceManager.getInstance( this ).getUserId();

		senderName = PreferenceManager.getInstance(this).getUserName();
		senderId = PreferenceManager.getInstance(this).getUserId();


		linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		linearLayoutManager.setStackFromEnd(true);
		recyclerViewChat.setLayoutManager(linearLayoutManager);

		chatAdapter = new ChatAdapter(this, listMessageModel, senderId);
		recyclerViewChat.setAdapter(chatAdapter);

		initializeView();

		linearLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
		linearLayoutManager.setStackFromEnd( true );
		recyclerViewChat.setLayoutManager( linearLayoutManager );


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
					MessageModel modelChat = innerDataSanpShot.getValue( MessageModel.class );
					Log.i( TAG, "onDataChange: 1 " + modelChat.getSenderName() + " \n " + modelChat.message );
					if ( !PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() ) {
						listMessageModel.add( modelChat );
					//	databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
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
		databaseReferenceChat.addValueEventListener( postListener );
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
	public void onClick( View view ) {
		switch ( view.getId() ) {
			case R.id.imageViewSend:
				message = emojiEditText.getText().toString().trim();
				if ( TextUtils.isEmpty( message ) ) {
					CommonUtils.showSnackBar( rootLayoutChat, "Please Enter Message", Snackbar.LENGTH_SHORT );
				}
				else {

							sendNewChat(message,ChatAdapter.ROW_TYPE_TEXT);

					}
					chatAdapter.notifyDataSetChanged();
					emojiEditText.setText( "" );


				break;
			case R.id.imageViewEmoji:
				emojIcon.ShowEmojIcon();
				break;
			case R.id.imageViewAttach:
				showSubjectBottomsheet();
				break;

			/*case R.id.buttonLoadMore:
				loadMorePosition = 1;
				if ( databaseHelper.getChatCount() == listModelChat.size() ) {
					CommonUtils.showSnackBar( coordinateLayoutRootChat, "No more Chat", Snackbar.LENGTH_SHORT );
				}
				else {
					if ( loadMorePosition > 2 ) {
						listModelChat = databaseHelper.getChat( ChatActivity.this, userIdRecicver );
					}
					else {
						listModelChat = databaseHelper.getChat( ChatActivity.this, userIdRecicver );
					}
					chatAdapter.notifyDataSetChanged();
				}
				buttonLoadMore.setVisibility( View.GONE );
				break;
			case R.id.imageViewAttachImageChat:
			//	selected_filetype = ImageVideoDialogFragment.FILE_TYPE_IMAGE;
				onUploadImage();
				// frameLayoutAttach.setVisibility( View.GONE );
				break;
			case R.id.imageViewAttachVideoChat:
			//	selected_filetype = ImageVideoDialogFragment.FILE_TYPE_VIDEO;
				onUploadImage();
				frameLayoutAttach.setVisibility( View.GONE );
				break;
			case R.id.imageViewAttachDocChat:
				frameLayoutAttach.setVisibility( View.GONE );
				break;

			case R.id.imageViewAttachLocChat:
				frameLayoutAttach.setVisibility( View.GONE );
				break;
			case R.id.relativeLayoutRecyclerView:
				frameLayoutAttach.setVisibility( View.GONE );
				break;
				*/
			case R.id.imageViewToolbarBack :
				onBackPressed();
				break;

		}


	}

	@Override
	public void onChildAdded(DataSnapshot dataSnapshot, String s ) {
		Log.i( TAG, "onChildAdded: " + s + "  spm " );
	}

	@Override
	public void onChildChanged(DataSnapshot dataSnapshot, String s ) {

	}

	@Override
	public void onChildRemoved( DataSnapshot dataSnapshot ) {

	}

	@Override
	public void onChildMoved(DataSnapshot dataSnapshot, String s ) {

	}

	@Override
	public void onCancelled( DatabaseError databaseError ) {

	}

	@Override
	public void onSnackbarActionClick() {
		if ( CommonUtils.isInternetAvailable( this ) ) {
			getChatData();
		}
		else {
			CommonUtils.showSnackBar( this, rootLayoutChat, getResources().getString( R.string.no_internet ), "Retry", Snackbar.LENGTH_SHORT );
		}
	}

	private void getChatData() {

		Query chatDetailsQuery = ChatApplication.getFirebaseDatabaseReference().
				child(Constants.FirebaseConstants.TABLE_CHAT).orderByChild("senderId").equalTo(recieverId);

		chatDetailsQuery.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				Log.i( TAG, "onDataChange: " );
				if ( dataSnapshot != null ) {

					Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
					Iterator<DataSnapshot> iterator             = dataSnapshotIterable.iterator();

					while ( iterator.hasNext() ) {
						Log.i( TAG, "onDataChange: 2 " );
						MessageModel messageModel = iterator.next().getValue( MessageModel.class );
						listMessageModel.add( messageModel );
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
	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
		super.onRequestPermissionsResult( requestCode, permissions, grantResults );
		if ( requestCode == PERMISSION_READ_WRITE_EXTERNAL_STORAGE ) {
			if ( grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
//				pickImage();
		//		ImageVideoDialogFragment.newInstance( true, selected_filetype, "" ).show( getSupportFragmentManager(), ImageVideoDialogFragment.TAG );
			}
		}
	}

	private void hideProgressDialog1() {
		if ( mProgressDialog != null && mProgressDialog.isShowing() ) {
			mProgressDialog.dismiss();
		}
	}

	private void showToast( String message ) {
		Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
	}

	public void updateProgress( int progress ) {
		if ( mProgressDialog != null && mProgressDialog.isShowing() ) {
			mProgressDialog.setProgress( progress );
			if ( progress == 100 ) {
				hideProgressDialog1();
				Toast.makeText( ChatActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT ).show();
			}
		}

	}


	private boolean isRecyclerAtTop() {
		if ( recyclerViewChat.getChildCount() == 0 ) {
			return true;
		}
		return recyclerViewChat.getChildAt( 0 ).getTop() == 0;
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

	private void sendNewChat(String message,int messageType) {
		/**
		 * String chatKey, String senderName, String senderId,
		 String message, String messageTimeInMillis, String messageDate,
		 String messageTime, int messageType, String pathOrUrl, int isDownloaded,
		 int isSent, String groupName, int isTeacher, int isCurrentUser,int isAccepted
		 */

		MessageModel messageModel = new MessageModel(
				"",senderId,senderName,senderId,message,"" + System.currentTimeMillis(),
				CommonUtils.getCurrentDate(),CommonUtils.getCurrentTime(),
				messageType,"",0,0,groupName,
				PreferenceManager.getInstance(this).getIsStudent()?0:1,
				0
		);


		if ( CommonUtils.isInternetAvailable( this ) ) {
			String key = databaseChatReference.getKey();
			String insertKey = databaseChatReference.push().getKey();
			messageModel.setChatKey(insertKey);
			try {
				databaseChatReference.child(Constants.FirebaseConstants.TABLE_CHAT).child(insertKey).setValue(messageModel);
			}
			catch (Exception e){
				e.printStackTrace();
				Log.i(TAG, e.getMessage());
			}
		}
		else {
			CommonUtils.showSnackBar( rootLayoutChat, "No Internet Connection", Snackbar.LENGTH_SHORT );
			appDatabase.getMessageDao().insertMessage(messageModel);
		}

		refreshChatList(messageModel);

	}


	private void refreshChatList(List<MessageModel> listMessage){
		if(listMessage!=null && listMessage.size()>0){
			int size = listMessageModel.size();
			listMessageModel.addAll(listMessage);
			chatAdapter.notifyItemRangeChanged(size,listMessageModel.size());

		}
	}
	private void refreshChatList(MessageModel messageModel){
		if(messageModel!=null){
			int size = listMessageModel.size();
			listMessageModel.add(messageModel);
			chatAdapter.notifyItemRangeChanged(size,listMessageModel.size());
		}
	}

	private void getAllGroupData(String subjectName){
		List<MessageModel> list = appDatabase.getMessageDao().getChatdataBySubject1(subjectName);
		List<MessageModel> list1 = appDatabase.getMessageDao().getAllChat();

		if(list!=null && list.size()>0){
			listMessageModel.addAll(list);
			refreshChatList(list);
		}

	}

	private void showProgress(boolean isShow){
		if(isShow){
			progressLoading.setVisibility(View.VISIBLE);
			imageViewSend.setClickable(false);
		}
		else{
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

       /* showProgress(true);

        uploadFileReference = mStorageReference.child(Constants.FirebaseConstants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + ".pdf");
        uploadFileReference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        uploadFileReference.getDownloadUrl();

                        showProgress(false);
                       Uri  fileUri = taskSnapshot.getUploadSessionUri();
                       String url = fileUri.toString();
                           progressDialog.dismiss();
                            String url = taskSnapshot.getDownloadUrl().toString();
                            if(courseDataAfterUpload!=null ) {
                             courseDataAfterUpload.setSyllabusFilePath(url);
                            FirebaseUtility.updateCourse(courseDataAfterUpload);

                            new TeacherDataManager(getActivity()).updateTeacherAddedCourse(courseDataAfterUpload);


                        //  FirebaseUtility.updateTeacherProfileData(courseDataAfterUpload.getCourseName());
                        Toast.makeText(SubjectChatActivity.this, "Success", Toast.LENGTH_LONG).show();
                        //  }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(SubjectChatActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    }
                });*/




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



}
