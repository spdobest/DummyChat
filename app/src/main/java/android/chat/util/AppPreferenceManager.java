/*
 * Copyright © 2016, Craftsvilla.com
 *  Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Mahesh Nayak on 19-02-2016.
 */
public class AppPreferenceManager {

	private static AppPreferenceManager     sInstance;
	private        SharedPreferences        mPrefs;
	private        SharedPreferences.Editor mEditor;

	private AppPreferenceManager( Context ctx ) {
		mPrefs = ctx.getApplicationContext().getSharedPreferences( ctx.getPackageName(), Context.MODE_PRIVATE );
		mEditor = mPrefs.edit();
	}

	public static AppPreferenceManager getInstance( Context ctx ) {
		if ( sInstance == null ) {
			sInstance = new AppPreferenceManager( ctx );
		}
		return sInstance;
	}

	public String getEmail() {
		return mPrefs.getString( Keys.EMAIL, Constants.EMPTY_TEXT );
	}

	public void setEmail( String name ) {
		mEditor.putString( Keys.EMAIL, name ).commit();
	}


	public String getRegstrationId() {
		return mPrefs.getString( Keys.REGISTRATION_ID, Constants.EMPTY_TEXT );
	}

	public void setRegstrationId( String deviceId ) {
		mEditor.putString( Keys.REGISTRATION_ID, deviceId ).commit();
	}


	public String getDeviceId() {
		return mPrefs.getString( Keys.DEVICE_ID, Constants.EMPTY_TEXT );
	}

	public void setDeviceId( String deviceId ) {
		mEditor.putString( Keys.DEVICE_ID, deviceId ).commit();
	}

	public String getUserName() {
		return mPrefs.getString( Keys.NAME, Constants.EMPTY_TEXT );
	}

	public void setUserName( String name ) {
		mEditor.putString( Keys.NAME, name ).commit();
	}


	public String getGroupId() {
		return mPrefs.getString( Keys.GROUP_ID, Constants.EMPTY_TEXT );
	}

	public void setGroupId( String groupId ) {
		mEditor.putString( Keys.GROUP_ID, groupId ).commit();
	}

	public String getGroupName() {
		return mPrefs.getString( Keys.GROUP_NAME, Constants.EMPTY_TEXT );
	}

	public void setGroupName( String groupName ) {
		mEditor.putString( Keys.GROUP_NAME, groupName ).commit();
	}


	public String getDob() {
		return mPrefs.getString( Keys.DOB, Constants.EMPTY_TEXT );
	}

	public void setDob( String dob ) {
		mEditor.putString( Keys.DOB, dob ).commit();
	}


	public String getMobileNumber() {
		return mPrefs.getString( Keys.MOBILE_NO, Constants.EMPTY_TEXT );
	}

	public void setMobileNumber( String mobileNo ) {
		mEditor.putString( Keys.MOBILE_NO, mobileNo ).commit();
	}


	public String getPassword() {
		return mPrefs.getString( Keys.PASSWORD, Constants.EMPTY_TEXT );
	}

	public void setPassword( String password ) {
		mEditor.putString( Keys.PASSWORD, password ).commit();
	}


	public interface Keys {
		String NAME                 = "name";
		String EMAIL                = "email";
		String REGISTRATION_ID      = "registrationId";
		String GROUP_NAME           = "groupName";
		String GROUP_ID             = "groupId";
		String DOB                  = "dob";
		String YATRA_NAME           = "yatraName";
		String PASSWORD             = "password";
		String DEVICE_ID            = "device_id";
		String MOBILE_NO            = "mobileNumber";
		String USER_REGISTRATOIN_ID = "userRegistrationId";
	}
}
