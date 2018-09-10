package android.chat.data;

import android.chat.model.chat.ModelChat;
import android.chat.room.entity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sibaprasad on 2/3/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	static final String CTEATE_TABLE_CHAT = "create table "
			+ DatabaseConstants.TABLE_CHAT + "(" + DatabaseConstants.ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DatabaseConstants.USER_ID + " TEXT NOT NULL, " + DatabaseConstants.USER_ID_RECIEVER + " TEXT NOT NULL, " + DatabaseConstants.USERNAME + " TEXT NOT NULL, "
			+ DatabaseConstants.MESSAGE + " TEXT NOT NULL, " + DatabaseConstants.TIME + " TEXT NOT NULL, "
			+ DatabaseConstants.PATH + " TEXT NOT NULL, " + DatabaseConstants.IS_DOWNLOADED + " INTEGER ,"
			+ DatabaseConstants.IS_SENT + " INTEGER , " + DatabaseConstants.OTHER_PATH + " TEXT NOT NULL, "
			+ DatabaseConstants.ROW_TYPE + " INTEGER ," + DatabaseConstants.CURRENT_TIME_MILLIES + " TEXT ," + DatabaseConstants.CHAT_KEY + " TEXT ," + DatabaseConstants.CHAT_DATE + " TEXT ); ";


	static final String CTEATE_TABLE_USER = "create table "
			+ DatabaseConstants.TABLE_USER + "(" + DatabaseConstants.ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DatabaseConstants.USER_ID + " TEXT NOT NULL, " + DatabaseConstants.USER_NAME + " TEXT NOT NULL, " + DatabaseConstants.EMAIL + " TEXT NOT NULL, "
			+ DatabaseConstants.MOBILE + " TEXT NOT NULL, " + DatabaseConstants.CHAT_DATE + " TEXT ); ";


	public static String TAG = DatabaseHelper.class.getSimpleName();


	public DatabaseHelper( Context context ) {
		super( context, DatabaseConstants.DB_NAME, null, 1 );
	}


	@Override
	public void onCreate( SQLiteDatabase db ) {
		db.execSQL( CTEATE_TABLE_CHAT );
		db.execSQL( CTEATE_TABLE_USER );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		db.execSQL( "DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_CHAT );
		db.execSQL( "DROP TABLE IF EXISTS " + DatabaseConstants.TABLE_USER );
		onCreate( db );
	}

	//  check if item exists or not
	public boolean isNewsIdExist( String news_id ) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from " + DatabaseConstants.TABLE_CHAT + " where" + DatabaseConstants.ROW_ID + " = ?",
				new String[]{ news_id } );
		boolean exists = ( cursor.getCount() > 0 );
		cursor.close();
		Log.i( TAG, "isNewsIdExist: " + exists );
		return exists;
	}

	//================================================== USER ============================================
	//  check if item exists or not
	public boolean isUserIdExist(Context context , String userId ) {
		boolean exists = false;
		if(!PreferenceManager.getInstance( context ).getIsUserExist()) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select * from " + DatabaseConstants.TABLE_USER + " where " + DatabaseConstants.USER_ID + " = ?",
					new String[]{ userId } );
			exists = ( cursor.getCount() > 0 );
			cursor.close();
			Log.i( TAG, "isNewsIdExist: " + exists );
		}
		return exists;
	}

	// insert Mp3 files
	public void insertUser( Context context, User user ) {


		if(!isUserIdExist( context,user.getUserId() )) {

			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();

			values.put( DatabaseConstants.USER_ID, user.getUserId() );
			values.put( DatabaseConstants.USER_NAME, user.getUserName() );
			values.put( DatabaseConstants.EMAIL, user.getEmailId() );
			values.put( DatabaseConstants.MOBILE, user.getMobileNumber() );
			long result = db.insert( DatabaseConstants.TABLE_USER, null, values );
			if(result>0)
				PreferenceManager.getInstance( context ).setUserExist( true );
			Log.i( TAG, "insertChat: " + result );
			db.close(); // Closing database connection
		}
	}

	// get the all mp3 files
	public ArrayList<User> getUser(Context context ) {

		ArrayList< User > mediaList = new ArrayList< User >();
		if ( PreferenceManager.getInstance( context ).getIsChatExist() ) {
			SQLiteDatabase db = this.getReadableDatabase();


			User user = null;
//					select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;
			Cursor cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_USER + " ORDER BY rowId ASC", null );
//					 ORDER BY message_id DESC limit dd


			cursor.moveToFirst();


			do {
				user = new User( cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID ) ),
				                 cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_NAME ) ),
				                 cursor.getString( cursor.getColumnIndex( DatabaseConstants.EMAIL ) ),
				                 cursor.getString( cursor.getColumnIndex( DatabaseConstants.MOBILE ) ),
				                 cursor.getString( cursor.getColumnIndex( DatabaseConstants.GENDER ) ),
						""
				);
				mediaList.add( user );

			}
			while ( cursor.moveToNext() );
			db.close();
		}
		return mediaList;
	}
	//========================================END USER============================================

	// insert Mp3 files
	public void insertChat(Context context, ModelChat modelChat, boolean isChatExist ) {

		if ( !isChatExist( modelChat.currentTimeInMillies, modelChat.message, isChatExist ) ) {

			PreferenceManager.getInstance( context ).setChatExist( true );

			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();

			values.put( DatabaseConstants.USER_ID, modelChat.userId );
			values.put( DatabaseConstants.USER_ID_RECIEVER, modelChat.recieverUserId );
			values.put( DatabaseConstants.USERNAME, modelChat.userName );
			values.put( DatabaseConstants.MESSAGE, modelChat.message );
			values.put( DatabaseConstants.TIME, modelChat.time );
			values.put( DatabaseConstants.ROW_TYPE, modelChat.rowType );
			values.put( DatabaseConstants.PATH, modelChat.PathOrUrl );
			values.put( DatabaseConstants.IS_DOWNLOADED, modelChat.isDownloaded );
			values.put( DatabaseConstants.IS_SENT, modelChat.isSent );
			values.put( DatabaseConstants.OTHER_PATH, modelChat.extraData );
			values.put( DatabaseConstants.CURRENT_TIME_MILLIES, modelChat.currentTimeInMillies );
			values.put( DatabaseConstants.CHAT_DATE, modelChat.chatDate );
			long result = db.insert( DatabaseConstants.TABLE_CHAT, null, values );
			Log.i( TAG, "insertChat: " + result );
			db.close(); // Closing database connection
		}
		else {
			Log.i( TAG, "insertChat: " );
		}
	}

	// get the all mp3 files
	public ArrayList< ModelChat > getChat( Context context, String userId ) {

		ArrayList< ModelChat > mediaList = new ArrayList< ModelChat >();
		if ( PreferenceManager.getInstance( context ).getIsChatExist() ) {
			SQLiteDatabase db = this.getReadableDatabase();


			ModelChat modelChat;
//					select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;
//			Cursor cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT + " ORDER BY rowId ASC", null );
//					 ORDER BY message_id DESC limit dd

			Cursor cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT +
					                             " where " + DatabaseConstants.USER_ID_RECIEVER + " = ? ", new String[]{ userId } );


			if ( cursor.getCount() > 0 ) {

				cursor.moveToFirst();


				do {
//            ModelNews(String id, String name, String desc, String date,String url,String likes,int isRead){
					modelChat = new ModelChat( cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID_RECIEVER ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USERNAME ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.MESSAGE ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.TIME ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.ROW_TYPE ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.PATH ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_DOWNLOADED ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_SENT ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.OTHER_PATH ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CURRENT_TIME_MILLIES ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_KEY ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_DATE ) ),
												cursor.getInt( cursor.getColumnIndex( DatabaseConstants.CHAT_DATE ) )

					);
					mediaList.add( modelChat );

				}
				while ( cursor.moveToNext() );
				db.close();
			}
		}
		return mediaList;
	}

	public ArrayList< ModelChat > getLast20Chat( Context context, int count ) {
		ArrayList< ModelChat > mediaList = new ArrayList< ModelChat >();
		if ( PreferenceManager.getInstance( context ).getIsChatExist() ) {
			Cursor         cursor;
			SQLiteDatabase db = this.getReadableDatabase();


			if ( getChatCount() > count ) {
				cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT + " ORDER BY rowId ASC limit " + 20, null );
			}
			else {
				cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT + " ORDER BY rowId ASC ", null );
			}


			ModelChat modelChat;

//					 ORDER BY message_id DESC limit dd
			cursor.moveToFirst();


			do {
//            ModelNews(String id, String name, String desc, String date,String url,String likes,int isRead){
				modelChat = new ModelChat( cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID_RECIEVER ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USERNAME ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.MESSAGE ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.TIME ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.ROW_TYPE ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.PATH ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_DOWNLOADED ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_SENT ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.OTHER_PATH ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CURRENT_TIME_MILLIES ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_KEY ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_DATE ) ),
						cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_ACCEPTED ) )
				);
				mediaList.add( modelChat );

			}
			while ( cursor.moveToNext() );
			db.close();
		}
		return mediaList;
	}


	// get the all mp3 files
	public ArrayList< ModelChat > getUnsentChat( Context context ) {

		ArrayList< ModelChat > mediaList = new ArrayList< ModelChat >();
		if ( PreferenceManager.getInstance( context ).getIsChatExist() ) {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = null;
			ModelChat modelChat;
//					select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;
//			Cursor cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT + " ORDER BY rowId ASC", null );
//					 ORDER BY message_id DESC limit dd

			try{
			cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT +
					                             " where " + DatabaseConstants.IS_SENT + " = ? ", new String[]{ "0" } );


			if ( cursor.getCount() > 0 ) {

				cursor.moveToFirst();


				do {
//            ModelNews(String id, String name, String desc, String date,String url,String likes,int isRead){
					modelChat = new ModelChat( cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID_RECIEVER ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USERNAME ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.MESSAGE ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.TIME ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.ROW_TYPE ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.PATH ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_DOWNLOADED ) ),
					                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_SENT ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.OTHER_PATH ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CURRENT_TIME_MILLIES ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_KEY ) ),
					                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_DATE ) ),
							cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_ACCEPTED ) )
					);
					mediaList.add( modelChat );

				}
				while ( cursor.moveToNext() );

			}
		}catch ( Exception e ){

			}
			finally {
				if (cursor != null && !cursor.isClosed())
					cursor.close();
			}
			db.close();
		}
		return mediaList;
	}


	// get the all mp3 files
	public ModelChat getLastChat( Context context ) {
		ModelChat modelChat = null;
		if ( PreferenceManager.getInstance( context ).getIsChatExist() ) {
			SQLiteDatabase db     = this.getReadableDatabase();
			Cursor         cursor = db.rawQuery( "select * from " + DatabaseConstants.TABLE_CHAT + " ORDER BY rowId ASC", null );

			cursor.moveToPosition( cursor.getCount() - 1 );

			do {
//            ModelNews(String id, String name, String desc, String date,String url,String likes,int isRead){
				modelChat = new ModelChat( cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USER_ID_RECIEVER ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.USERNAME ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.MESSAGE ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.TIME ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.ROW_TYPE ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.PATH ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_DOWNLOADED ) ),
				                           cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_SENT ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.OTHER_PATH ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CURRENT_TIME_MILLIES ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_KEY ) ),
				                           cursor.getString( cursor.getColumnIndex( DatabaseConstants.CHAT_DATE ) ),
						cursor.getInt( cursor.getColumnIndex( DatabaseConstants.IS_ACCEPTED ) )
				);

			}
			while ( cursor.moveToNext() );
			db.close();
		}
		return modelChat;
	}

	//  check if item exists or not
	public boolean isChatExist( String currentTimeInMillies, String chat, boolean isChatExist ) {
		boolean exists = false;
		if ( isChatExist ) {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select * from " + DatabaseConstants.TABLE_CHAT + " where " + DatabaseConstants.CURRENT_TIME_MILLIES + " = ?",
					new String[]{ currentTimeInMillies } );
			Cursor cursor1 = db.rawQuery(
					"select * from " + DatabaseConstants.TABLE_CHAT + " where " + DatabaseConstants.MESSAGE + " = ?",
					new String[]{ chat } );

			exists = ( cursor.getCount() > 0 ) && ( cursor1.getCount() > 0 );
			cursor.close();
		}
		return exists;
	}

	public boolean isTableHaveData( String table_name ) {
		Log.i( TAG, "isTableHaveData: 1" );
		boolean        isEmpty = false;
		SQLiteDatabase db      = getWritableDatabase();
		String         count   = "SELECT count(*) FROM " + table_name;
		Cursor         mcursor = db.rawQuery( count, null );
		Log.i( TAG, "isTableHaveData: 2" );
		mcursor.moveToFirst();
		Log.i( TAG, "isTableHaveData: 3 " + mcursor );
		int icount = mcursor.getInt( 0 );

		Log.i( TAG, "isTableHaveData: 4 " + icount );

		if ( mcursor != null && icount > 0 )
		//leave
		{
			isEmpty = true;
		}
		else
		//populate table
		{
			isEmpty = false;
		}

		return isEmpty;
	}

	public boolean isTableEmpty( String TableName ) {
		boolean        bool    = false;
		SQLiteDatabase db      = getWritableDatabase();
		String         count   = "SELECT count(*) FROM " + TableName;
		Cursor         mcursor = db.rawQuery( count, null );
		mcursor.moveToFirst();
		int icount = mcursor.getInt( 0 );
	  /*  if(icount>0)
	        //leave
            bool = false;
        else
            //populate table
            bool = true;*/

		return ( icount > 0 ) ? true : false;
	}

	public void clearData( String table_name ) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL( "delete from " + table_name );
	}

	public int updateChatKey( String currentTimeInMillies, String chatKey ) {

		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put( DatabaseConstants.CHAT_KEY, chatKey ); // get title

		// 3. updating row
		int i = db.update( DatabaseConstants.TABLE_CHAT, // table
		                   values, // column/value
		                   DatabaseConstants.CURRENT_TIME_MILLIES + " = ?", // selections
		                   new String[]{ String.valueOf( currentTimeInMillies ) } ); // selection args
		// 4. close
		db.close();
		return i;
	}

	public int updateSentStatus( String currentTimeInMillies, String chatKey ) {

		SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
		ContentValues values = new ContentValues();
		values.put( DatabaseConstants.CHAT_KEY, chatKey ); // get title
		values.put( DatabaseConstants.IS_SENT, 1 );
		// 3. updating row
		int i = db.update( DatabaseConstants.TABLE_CHAT, // table
		                   values, // column/value
		                   DatabaseConstants.CURRENT_TIME_MILLIES + " = ?", // selections
		                   new String[]{ String.valueOf( currentTimeInMillies ) } ); // selection args
		// 4. close
		db.close();
		return i;
	}

	public int getChatCount() {
		String         countQuery = "SELECT  * FROM " + DatabaseConstants.TABLE_CHAT;
		SQLiteDatabase db         = this.getReadableDatabase();
		Cursor         cursor     = db.rawQuery( countQuery, null );
		int            cnt        = cursor.getCount();
		cursor.close();
		return cnt;
	}

	public interface DatabaseConstants {
		static String DB_NAME              = "Chatdatabase";
		static String TABLE_CHAT           = "TableChat";
		static String ROW_ID               = "rowId";
		static String USER_ID              = "userId";
		static String USER_ID_RECIEVER     = "userIdReciever";
		static String USERNAME             = "userName";
		static String MESSAGE              = "message";
		static String TIME                 = "time";
		static String ROW_TYPE             = "rowType";
		static String PATH                 = "path";
		static String IS_DOWNLOADED        = "isDownloaded";
		static String IS_SENT              = "isSent";
		static String OTHER_PATH           = "otherPath";
		static String CURRENT_TIME_MILLIES = "currenttimeinmillies";
		static String CHAT_KEY             = "chat_key";
		static String CHAT_DATE            = "CHAT_DATE";
		static String IS_ACCEPTED            = "isAccepted";

		static String TABLE_USER = "TableUser";
		static String USER_NAME  = "userName";
		static String EMAIL      = "email";
		static String MOBILE     = "mobile";
		static String GENDER     = "userId";
	}
}
