package android.chat.adapter;

import android.chat.R;
import android.chat.room.entity.User;
import android.chat.ui.activity.ChatActivity;
import android.chat.util.Constants;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sibaprasad on 01/01/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ChatViewHolder > {

	private Context     context;
	private List<User> listData;
	LayoutInflater layoutInflater;

	public UserAdapter(Context context, List< User > listData ) {
		this.context = context;
		this.listData = listData;
		layoutInflater = LayoutInflater.from( context );
	}

	@Override
	public  ChatViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		View  view           = layoutInflater.inflate( R.layout.itemview_user, parent, false );
		ChatViewHolder chatViewHolder = new ChatViewHolder( view );
		return chatViewHolder;
	}

	@Override
	public void onBindViewHolder(final  ChatViewHolder holder,final int position ) {
		/*Picasso.with( context)
				.load(listData.get( position ).imageUrl)
				.error(R.drawable.ic_launcher)         // optional
				.into(holder.imageviewItemChatListMain);*/

		final User user =  listData.get( position );

		holder.textViewNameChatList.setText( user.getEmailId() );
		holder.textViewLastChat.setText( user.getMobileNumber() );
		holder.textViewTimeChatList.setText( user.getGender() );

		holder.relativeLayoutRootMainChatlist.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				/*Intent intentChat = new Intent( context, ChatActivity.class);
				intentChat.putExtra( Constants.BundleKeys.USER_NAME, user.getUserName() );
				intentChat.putExtra( Constants.BundleKeys.USER_ID, user.getUserId() );
				intentChat.putExtra( Constants.BundleKeys.EMAIL, user.getEmailId() );
				context.startActivity( intentChat );*/
			}
		} );


		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intentChat = new Intent(holder.itemView.getContext(),ChatActivity.class);
				intentChat.putExtra( Constants.BundleKeys.USER_NAME, user.getUserName() );
				intentChat.putExtra( Constants.BundleKeys.USER_ID, user.getUserId() );
				intentChat.putExtra( Constants.BundleKeys.EMAIL, user.getEmailId() );
				context.startActivity(intentChat);
			}
		});

	}

	@Override
	public int getItemCount() {
		return listData.size();
	}

	public class ChatViewHolder extends RecyclerView.ViewHolder{
		RelativeLayout    relativeLayoutRootMainChatlist;
		CircleImageView imageviewItemChatListMain;
		AppCompatTextView textViewNameChatList;
		AppCompatTextView textViewLastChat;
		AppCompatTextView textViewTimeChatList;
		public ChatViewHolder( View itemView ) {
			super( itemView );
			relativeLayoutRootMainChatlist = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutRootMainChatlist );
			imageviewItemChatListMain = (CircleImageView) itemView.findViewById( R.id.imageviewItemChatListMain );
			textViewNameChatList = (AppCompatTextView) itemView.findViewById( R.id.textViewNameChatList );
			textViewLastChat = (AppCompatTextView) itemView.findViewById( R.id.textViewLastChat );
			textViewTimeChatList = (AppCompatTextView) itemView.findViewById( R.id.textViewTimeChatList );
		}
	}
}
