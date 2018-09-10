package android.chat.model.chat;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sibaprasad on 24/12/16.
 */
@IgnoreExtraProperties
public class ModelChat {

	public String userId;
	public String recieverUserId;
	public String userName;
	public String message;
	public String time;
	public int rowType;
	public String PathOrUrl;
	public int isDownloaded;
	public int isSent;
	public String extraData;
	public String currentTimeInMillies;
	public String chatKey;
	public String chatDate;
	public int isAccepted;

	public ModelChat() {
		// Default constructor required for calls to DataSnapshot.getValue(User.class)
	}

	public ModelChat( String userId,String recieverUserId,
					  String userName, String message,String time,
					  int rowType, String PathOrUrl,int isDownloaded ,
					  int isSent ,String extraData,String currrentTimeinMillies,
					  String chatKey,String chatDate,
					  int isAccepted) {
		this.userId = userId;
		this.recieverUserId = recieverUserId;
		this.userName = userName;
		this.message = message;
		this.time = time;
		this.rowType = rowType;
		this.PathOrUrl = PathOrUrl;
		this.isDownloaded = isDownloaded;
		this.isSent = isSent;
		this.extraData = extraData;
		this.currentTimeInMillies = currrentTimeinMillies;
		this.chatKey = chatKey;
		this.chatDate = chatDate;
		this.isAccepted = isAccepted;
	}

	@Exclude
	public Map<String, Object> toMap() {
		HashMap<String, Object> result = new HashMap<>();
		result.put("userId", userId);
		result.put("recieverUserId", recieverUserId);
		result.put("userName", userName);
		result.put("message", message);
		result.put("time", time);
		result.put("PathOrUrl", PathOrUrl);
		result.put("isDownloaded", isDownloaded);
		result.put("isSent", isSent);
		result.put("extraData", extraData);
		result.put( "currrentTimeinMillies",currentTimeInMillies );
		result.put( "chatDateAndTime",chatDate );
		result.put( "isAccepted",isAccepted );
		return result;
	}

	public String getChatDate() {
		return chatDate;
	}

	public void setChatDate( String chatDate ) {
		this.chatDate = chatDate;
	}

	public String getChatKey() {
		return chatKey;
	}

	public void setChatKey( String chatKey ) {
		this.chatKey = chatKey;
	}
}
