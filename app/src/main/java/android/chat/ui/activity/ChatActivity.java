package android.chat.ui.activity;

import android.Manifest;
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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
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
	private final static int      SELECT_PICTURE                         = 1;
	///////////////upload images/////////////
	private static final int      REQUEST_CODE_PICK_IMAGE                = 1;
	private static final int      PERMISSION_READ_WRITE_EXTERNAL_STORAGE = 2;
	private final        Activity current                                = this;
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

	// other class declaration
	private ChatAdapter chatAdapter;
	private LinearLayoutManager linearLayoutManager;

	private ProgressDialog     mProgressDialog;
	private Uri                mUri;

	// variables
	private String              strName;
	private String              userId;
	private String userIdRecicver;
	private String userNameRecicver;



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

	EmojIconActions emojIcon;


	private DatabaseHelper databaseHelper;
	private int[] chatCount        = { 20, 50, 100, };
	private int   loadMorePosition = 0;

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


		Intent intentBundle = getIntent();
		if(intentBundle.hasExtra( Constants.BundleKeys.USER_NAME ))
			userNameRecicver = intentBundle.getExtras().getString(  Constants.BundleKeys.USER_NAME );
		if(intentBundle.hasExtra( Constants.BundleKeys.USER_ID ))
			userIdRecicver = intentBundle.getExtras().getString(  Constants.BundleKeys.USER_ID );

		strName = PreferenceManager.getInstance( this ).getUserName();
		userId = PreferenceManager.getInstance( this ).getUserId();

		// database initiallization
		databaseHelper = new DatabaseHelper( ChatActivity.this );

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
		databaseReference = firebaseDatabase.getReference( Constants.FirebaseConstants.TABLE_CHAT );
		databaseChatReference = FirebaseDatabase.getInstance().getReference();

		//	sendNewChat(userId,strName,"Welcome to sp chat",image,video,file,time,chatType,true);

		initializeView();

		linearLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false );
		linearLayoutManager.setStackFromEnd( true );
		recyclerViewChat.setLayoutManager( linearLayoutManager );

		if ( PreferenceManager.getInstance( this ).getIsChatExist() ) {
			listModelChat = databaseHelper.getChat( ChatActivity.this,userIdRecicver );
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
					if ( !PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() ) {
						listModelChat.add( modelChat );
						databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
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
	public void onClick( View view ) {
		switch ( view.getId() ) {
			case R.id.imageViewSend:
				message = emojiEditText.getText().toString().trim();
				ModelChat modelChat = null;
				if ( TextUtils.isEmpty( message ) ) {
					CommonUtils.showSnackBar( rootLayoutChat, "Please Enter Message", Snackbar.LENGTH_SHORT );
				}
				else {

					if ( PreferenceManager.getInstance( this ).getIsChatExist() ) {

						Log.i( TAG, "onClick: time  " + databaseHelper.getLastChat( ChatActivity.this ).message );
						if ( !CommonUtils.getCurrentDateAndTime().contains( getTimeFromDate( databaseHelper.getLastChat( ChatActivity.this ).chatDate ) ) ) {

							modelChat = new ModelChat( userId, userIdRecicver, strName, "", CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDate() );
							listModelChat.add( modelChat );
							databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );


							modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), ChatAdapter.ROW_TYPE_TEXT, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDate() );
							databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
							listModelChat.add( modelChat );

							if ( CommonUtils.isInternetAvailable( this ) ) {
								sendNewChat( userId, strName, message, CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 1, 1, "", System.currentTimeMillis() + "" );
								sendNewChat( modelChat );
							}

						}
						else {
							modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), ChatAdapter.ROW_TYPE_TEXT, "", 1, 1, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDate() );
							databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
							listModelChat.add( modelChat );
							if ( CommonUtils.isInternetAvailable( this ) ) {
								sendNewChat( modelChat );
							}
						}

						chatAdapter.notifyDataSetChanged();
					}
					else {
						modelChat = new ModelChat( userId, userIdRecicver, strName, "", CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDate() );
						Log.i( TAG, "onClick: " + modelChat.chatDate );
						databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
						listModelChat.add( modelChat );

						modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), ChatAdapter.ROW_TYPE_TEXT, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDate() );
						Log.i( TAG, "onClick: " + modelChat.chatDate );
						databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
						listModelChat.add( modelChat );

						if ( CommonUtils.isInternetAvailable( this ) ) {
							sendNewChat( userId, strName, message, CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 1, 1, "", System.currentTimeMillis() + "" );
							sendNewChat( modelChat );
						}

					}
					chatAdapter.notifyDataSetChanged();
					emojiEditText.setText( "" );

				}
				break;
			case R.id.imageViewEmoji:
				emojIcon.ShowEmojIcon();
				break;
			case R.id.imageViewAttach:

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

	public void sendNewChat( String userId, String username, String message, String time, int rowType, String pathOrUrl, int isDownloaded, int isSent, String extraData, String currentTimeinMillies ) {
		ModelChat modelChat = new ModelChat( userId,userIdRecicver, username, message, time, rowType, pathOrUrl, isDownloaded, isSent, extraData, currentTimeinMillies, "", CommonUtils.getCurrentDate() );
		databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );

//			databaseChatReference.child( Constants.FirebaseConstants.TABLE_CHAT ).setValue( modelChat );
		String key = databaseChatReference.getKey();
//		databaseChatReference.child( key ).setValue( modelChat );
//		databaseChatReference.push().setValue( modelChat );
//		databaseChatReference.setValue( modelChat );


		String insertKey = databaseChatReference.push().getKey();
		// pushing user to 'users' node using the userId
//		databaseChatReference.child(userId).setValue(modelChat);
		databaseChatReference.child( Constants.FirebaseConstants.TABLE_CHAT ).child( insertKey ).setValue( modelChat );

	}

	public void sendNewChat( ModelChat modelChat ) {
		String key       = databaseChatReference.getKey();
		String insertKey = databaseChatReference.push().getKey();
		// pushing user to 'users' node using the userId
//		databaseChatReference.child(userId).setValue(modelChat);
		databaseChatReference.child( Constants.FirebaseConstants.TABLE_CHAT ).child( insertKey ).setValue( modelChat );
	}


	private void setEmojiconBottomsheet( boolean useSystemDefault ) {

		/*if ( bottomSheetDialog == null ) {
			bottomSheetDialog = new BottomSheetDialog( ChatActivity.this );
		}
		//similar
		bottomSheetDialog.setContentView( R.layout.bottomsheet_emoji );
		getSupportFragmentManager()
				.beginTransaction()
				.replace( R.id.linearLayoutBottomsheetEmoji, EmojiconsFragment.newInstance( useSystemDefault ) )
				.commit();
		bottomSheetDialog.show();*/


	}

	public void onUploadImage() {
		Log.i( TAG, "onUploadFileClick: imageViewAttach " );
		if ( ContextCompat.checkSelfPermission( ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission( ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {
			ActivityCompat.requestPermissions( ChatActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, PERMISSION_READ_WRITE_EXTERNAL_STORAGE );
		}
		else {

		}
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

	private void showProgressDialog( String title, String message ) {
		if ( mProgressDialog != null && mProgressDialog.isShowing() ) {
			mProgressDialog.setMessage( message );
		}
		else {
			mProgressDialog = ProgressDialog.show( this, title, message, true, false );
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

	public void showHorizontalProgressDialog( String title, String body ) {

		if ( mProgressDialog != null && mProgressDialog.isShowing() ) {
			mProgressDialog.setTitle( title );
			mProgressDialog.setMessage( body );
		}
		else {
			mProgressDialog = new ProgressDialog( this );
			mProgressDialog.setTitle( title );
			mProgressDialog.setMessage( body );
			mProgressDialog.setIndeterminate( false );
			mProgressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
			mProgressDialog.setProgress( 0 );
			mProgressDialog.setMax( 100 );
			mProgressDialog.setCancelable( false );
			mProgressDialog.show();
		}
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

	public void uploadFile( Uri uri ) {
		StorageReference uploadStorageReference = mStorageReferenceImages.child(/*uri.getLastPathSegment()*/"mountains.jpg" );
		final UploadTask uploadTask             = uploadStorageReference.putFile( uri );
		showHorizontalProgressDialog( "Uploading", "Please wait..." );
		uploadTask
				.addOnSuccessListener( new OnSuccessListener< UploadTask.TaskSnapshot >() {
					@Override
					public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
						hideProgressDialog();
						Uri downloadUrl = null;//taskSnapshot.getDownloadUrl();
						Log.d( "MainActivity", downloadUrl.toString() );
						sendNewChat( userId, strName, "", CommonUtils.getCurrentDateAndTime(), ChatAdapter.ROW_TYPE_IMAGE, downloadUrl.toString(), 1, 1, "", System.currentTimeMillis() + "" );
						Picasso.with( ChatActivity.this )
								.load( downloadUrl )
								.into( imageViewAttach );
					}
				} )
				.addOnFailureListener( new OnFailureListener() {
					@Override
					public void onFailure( @NonNull Exception exception ) {
						exception.printStackTrace();
						// Handle unsuccessful uploads
						hideProgressDialog();
					}
				} ).addOnProgressListener( ChatActivity.this, new OnProgressListener< UploadTask.TaskSnapshot >() {
			@Override
			public void onProgress( UploadTask.TaskSnapshot taskSnapshot ) {
				int progress = ( int ) ( 100 * ( float ) taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount() );
				Log.i( "Progress", progress + "" );
				updateProgress( progress );
			}
		} );
	}

	private String getTimeFromDate( String dateTime ) {
		String time = "";
		if ( !TextUtils.isEmpty( dateTime ) ) {
			time = ( dateTime.length() > 10 ) ? dateTime.substring( 0, 11 ) : dateTime.substring( 0, 10 );
		}
		else {
			String dt = CommonUtils.getCurrentDateAndTime();
			time = dt.substring( 11 );
		}
		return time;
	}

	public void sendImageToServer( String filePath, String message, short rowType ) {
		String path = SaveImage( filePath );

		if ( PreferenceManager.getInstance( this ).getIsChatExist() ) {
			ModelChat modelChat = null;
			if ( !CommonUtils.getCurrentDateAndTime().contains( getTimeFromDate( databaseHelper.getLastChat( ChatActivity.this ).chatDate ) ) ) {

				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				listModelChat.add( modelChat );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );


				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), rowType, path, 1, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
				listModelChat.add( modelChat );

				if ( CommonUtils.isInternetAvailable( this ) ) {
					sendNewChat( userId, strName, message, CommonUtils.getCurrentDateAndTime(), ChatAdapter.ROW_TYPE_DATE, "", 1, 1, "", System.currentTimeMillis() + "" );
					sendNewChat( modelChat );
				}

			}
			else {
				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), rowType, path, 1, 1, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );

				listModelChat.add( modelChat );
			}

			chatAdapter.notifyDataSetChanged();
		}

	}

	public void sendVideoToServer( String filePath, String message, short rowType ) {
		String path = SaveImage( filePath );

		if ( PreferenceManager.getInstance( this ).getIsChatExist() ) {
			ModelChat modelChat = null;
			if ( !CommonUtils.getCurrentDateAndTime().contains( getTimeFromDate( databaseHelper.getLastChat( ChatActivity.this ).chatDate ) ) ) {

				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentDate(), ChatAdapter.ROW_TYPE_DATE, "", 0, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				listModelChat.add( modelChat );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );


				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), rowType, path, 1, 0, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );
				listModelChat.add( modelChat );

				if ( CommonUtils.isInternetAvailable( this ) ) {
					sendNewChat( userId, strName, message, CommonUtils.getCurrentDateAndTime(), ChatAdapter.ROW_TYPE_DATE, "", 1, 1, "", System.currentTimeMillis() + "" );
					sendNewChat( modelChat );
				}

			}
			else {
				modelChat = new ModelChat( userId, userIdRecicver, strName, message, CommonUtils.getCurrentTime(), rowType, path, 1, 1, "", System.currentTimeMillis() + "", "", CommonUtils.getCurrentDateAndTime() );
				databaseHelper.insertChat( ChatActivity.this, modelChat, PreferenceManager.getInstance( ChatActivity.this ).getIsChatExist() );

				listModelChat.add( modelChat );
			}

			chatAdapter.notifyDataSetChanged();
		}

	}

	private String SaveImage( String filePath ) {
		String path        = "";
		Bitmap finalBitmap = BitmapFactory.decodeFile( filePath );

		File   myDir1 = null;
		String root   = Environment.getExternalStorageDirectory().toString();
		File   myDir  = new File( root + "/Spchatting" );
		if ( !myDir.exists() ) {
			myDir.mkdirs();
		}
		myDir1 = new File( myDir.getAbsolutePath() + "/images" );
		if ( !myDir1.exists() ) {
			myDir1.mkdir();
		}

		String fileName = "SpChatIMG" + System.currentTimeMillis() + filePath.substring( filePath.lastIndexOf( "." ) );

		File file = new File( myDir1, fileName );
		if ( file.exists() ) {
			file.delete();
		}

		path = file.getAbsolutePath();
		Log.i( TAG, "SaveImage: path is " + path );
		try {
			FileOutputStream out = new FileOutputStream( file );
			if ( fileName.endsWith( ".JPEG" ) || fileName.endsWith( ".JPG" ) || fileName.endsWith( ".jpeg" ) || fileName.endsWith( ".jpg" ) ) {
				finalBitmap.compress( Bitmap.CompressFormat.JPEG, 90, out );
			}
			else if ( fileName.endsWith( ".png" ) || fileName.endsWith( ".PNG" ) ) {
				finalBitmap.compress( Bitmap.CompressFormat.PNG, 90, out );
			}

			out.flush();
			out.close();

		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		return path;
	}

	private void loadImageFromSdcard( String path, AppCompatImageView imageview ) {
		Log.i( TAG, "loadImageFromSdcard: " + path );
		File imgFile = new File( path );
		if ( imgFile.exists() ) {
			Bitmap myBitmap = BitmapFactory.decodeFile( imgFile.getAbsolutePath() );

			imageview.setImageBitmap( myBitmap );
		}
	}

	private boolean isRecyclerAtTop() {
		if ( recyclerViewChat.getChildCount() == 0 ) {
			return true;
		}
		return recyclerViewChat.getChildAt( 0 ).getTop() == 0;
	}
}
