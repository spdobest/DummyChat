package android.chat.adapter;

import android.chat.R;
import android.chat.data.PreferenceManager;
import android.chat.listeners.OnActionListener;
import android.chat.room.entity.MessageModel;
import android.chat.util.ChatMessageDiffCallback;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by sibaprasad on 24/12/16.
 */

public class ChatAdapter extends RecyclerView.Adapter< ChatAdapter.ViewHolderParent > {

	public static final String TAG = "ChatAdapter";

	public static final int ROW_TYPE_TEXT  = 1;
	public  static final int ROW_TYPE_IMAGE = 2;
	public static final int ROW_TYPE_VIDEO = 3;
	public static final int ROW_TYPE_AUDIO = 4;
	public static final int ROW_TYPE_FILE = 5;

	public static final int ROW_TYPE_DATE = 6;

	LayoutInflater layoutInflater;
	private Context           context;
	private List<MessageModel> messageList;
	private String            userId;
	private String            userName;
	private OnActionListener onActionListener;

	public ChatAdapter(Context context, List<MessageModel> messageList, String userId,
					   OnActionListener onActionListener) {
		this.context = context;
		this.messageList = messageList;
		layoutInflater = LayoutInflater.from( context );
		this.userId = userId;
		this.onActionListener = onActionListener;
		userName = PreferenceManager.getInstance( context ).getUserName();
		Log.i( TAG, "ChatAdapter: user id " + userId + " size " + messageList.size() );
	}

	@Override
	public ViewHolderParent onCreateViewHolder( ViewGroup parent, int viewType ) {
		View v;
		if ( viewType == ROW_TYPE_TEXT ) {
			v = LayoutInflater.from( context )
					.inflate( R.layout.itemview_chat_text, parent, false );

			return new ViewHolderText( v );
		}
		else if ( viewType == ROW_TYPE_IMAGE ){
			v = LayoutInflater.from( parent.getContext() )
					.inflate( R.layout.itemview_chat_image, parent, false );

			return new ViewHolderImage( v );
		}
		else if ( viewType == ROW_TYPE_VIDEO ){
			v = LayoutInflater.from( parent.getContext() )
					.inflate( R.layout.itemview_chat_video, parent, false );

			return new ViewHolderVideo( v );
		}
		else if ( viewType == ROW_TYPE_AUDIO ){
			v = LayoutInflater.from( parent.getContext() )
					.inflate( R.layout.itemview_chat_video, parent, false );

			return new ViewHolderVideo( v );
		}
		/*else if ( viewType == ROW_TYPE_FILE ){
			v = LayoutInflater.from( parent.getContext() )
					.inflate( R.layout.itemview_doc, parent, false );

			return new ViewHolderDoc( v );
		}*/
		else{
			v = LayoutInflater.from( parent.getContext() )
					.inflate( R.layout.itemview_chat_date, parent, false );

			return new ViewHolderDate( v );
		}
	}

	@Override
	public void onBindViewHolder( ViewHolderParent holder, int position ) {
		final MessageModel messageModel = messageList.get( position );
		switch ( holder.getItemViewType() ) {
			case ROW_TYPE_TEXT:
				final ViewHolderText viewHolderText = ( ViewHolderText ) holder;
				viewHolderText.textViewUserNameChatText.setText( messageModel.getSenderName() );
				viewHolderText.textViewUserMessageChatText.setText( messageModel.getMessage() );
				viewHolderText.textViewUserTimeChatText.setText( messageModel.getMessageTime() );

				Log.i( TAG, "onBindViewHolder: "+userId.equalsIgnoreCase( messageModel.getSenderId() ) );

				if ( messageModel.getCurrentUserId().equalsIgnoreCase(userId) ) {
					//viewHolderText.textViewUserNameChatText.setVisibility(View.GONE);
					viewHolderText.linearLayoutChatBubble.setBackground(ContextCompat.getDrawable(viewHolderText.relativeLayoutRootChatText.getContext(),
							R.drawable.drawable_chatbuble_send));
					viewHolderText.relativeLayoutRootChatText.setGravity( Gravity.RIGHT );
					viewHolderText.imageViewRightArrowTextChat.setVisibility( View.GONE );
					viewHolderText.imageViewLeftArrowTextChat.setVisibility( View.GONE );
					viewHolderText.textViewUserMessageChatText.setGravity(Gravity.LEFT);
				}
				else {
					viewHolderText.relativeLayoutRootChatText.setGravity( Gravity.LEFT );
					viewHolderText.imageViewLeftArrowTextChat.setVisibility( View.GONE);
					viewHolderText.imageViewRightArrowTextChat.setVisibility( View.GONE );
					viewHolderText.textViewUserMessageChatText.setGravity(Gravity.LEFT);
					viewHolderText.linearLayoutChatBubble.setBackground(ContextCompat.getDrawable(viewHolderText.relativeLayoutRootChatText.getContext(),
							R.drawable.drawable_chatbuble_recieve));
				}

				/*if(messageModel.isSelected){
					viewHolderText.linearLayoutChatBubble.setBackgroundColor(ContextCompat.getColor(
							context,R.color.color_dark_grey
					));
				}
				else{
					viewHolderText.linearLayoutChatBubble.setBackgroundColor(ContextCompat.getColor(
							context,R.color.white
					));
				}*/


				viewHolderText.itemView.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View view) {
						if(messageModel.isSelected){
							messageModel.isSelected = false;
							viewHolderText.linearLayoutChatBubble.setBackgroundColor(ContextCompat.getColor(
									context, R.color.white
							));
							onActionListener.onLongPressSelect(messageModel);
						}
						else {
							messageModel.isSelected = true;
							viewHolderText.linearLayoutChatBubble.setBackgroundColor(ContextCompat.getColor(
									context, R.color.color_dark_grey
							));
							onActionListener.onLongPressSelect(messageModel);
						}
						return false;
					}
				});


				break;

			case ROW_TYPE_IMAGE:
				final ViewHolderImage viewHolderImage = ( ViewHolderImage ) holder;
				Log.i( TAG, "onBindViewHolder: image PATH  "+messageModel.getPathOrUrl() );
				viewHolderImage.textViewUserNameChatImage.setText( messageModel.getSenderName() );
				viewHolderImage.textViewUserMessageChatImage.setText( messageModel.getMessage() );
				viewHolderImage.textViewUserTimeChatImage.setText( getTimeFromDate( messageModel.getMessageTime() ) );
				if ( messageModel.getCurrentUserId().equalsIgnoreCase(userId) ) {
					viewHolderImage.relativeLayoutRootChatImage.setGravity( Gravity.RIGHT );
					viewHolderImage.imageViewRightArrowChatImage.setVisibility( View.VISIBLE );
					viewHolderImage.imageViewLeftArrowChatImage.setVisibility( View.GONE );
				}
				else {
					viewHolderImage.relativeLayoutRootChatImage.setGravity( Gravity.LEFT );
					viewHolderImage.imageViewLeftArrowChatImage.setVisibility( View.VISIBLE );
					viewHolderImage.imageViewRightArrowChatImage.setVisibility( View.GONE );
				}
				if(messageModel.isDownloaded == 0)
					Picasso.with(context)
						.load(messageModel.getPathOrUrl())
						.into(viewHolderImage.imageViewChatImage);
				else
					loadImageFromSdcard(messageModel.getPathOrUrl(),viewHolderImage.imageViewChatImage);
				viewHolderImage.imageViewChatImage.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick( View view ) {
						// ImageVideoDialogFragment.newInstance( false,ImageVideoDialogFragment.FILE_TYPE_IMAGE, MessageModel.PathOrUrl ).show( (( AppCompatActivity)context).getSupportFragmentManager(), ImageVideoDialogFragment.TAG );
					}
				} );


				break;
			case ROW_TYPE_VIDEO:
				final ViewHolderVideo viewHolderVideo = ( ViewHolderVideo ) holder;
				viewHolderVideo.textViewUserNameChatVideo.setText( messageModel.getSenderName() );
				viewHolderVideo.textViewUserMessageChatVideo.setText( messageModel.getMessage() );
				if ( messageModel.getCurrentUserId().equalsIgnoreCase(userId) ) {
					viewHolderVideo.relativeLayoutRootChatVideo.setGravity( Gravity.RIGHT );
					viewHolderVideo.imageViewRightArrowChatVideo.setVisibility( View.VISIBLE );
					viewHolderVideo.imageViewLeftArrowChatVideo.setVisibility( View.GONE );
				}
				else {
					viewHolderVideo.relativeLayoutRootChatVideo.setGravity( Gravity.LEFT );
					viewHolderVideo.imageViewLeftArrowChatVideo.setVisibility( View.VISIBLE );
					viewHolderVideo.imageViewRightArrowChatVideo.setVisibility( View.GONE );
				}
				Picasso.with(context)
						.load(messageModel.getPathOrUrl())
						.into(viewHolderVideo.imageViewChatVideo);
				break;
			case ROW_TYPE_DATE :
				final ViewHolderDate viewHolderDate = (ViewHolderDate) holder;
				viewHolderDate.textViewChatDate.setText( messageModel.getMessageDate() );
				break;

			/*case ROW_TYPE_FILE :
				final ViewHolderDoc viewHolderDoc = (ViewHolderDoc) holder;
				viewHolderDoc.textViewUserTimeChatVideo.setText( messageModel.getMessageTime());
				viewHolderDoc.buttonApprove.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {

					}
				});
				break;*/
		}
	}

	@Override
	public int getItemCount() {
		return messageList.size();
	}

	@Override
	public int getItemViewType( int position ) {
		return messageList.get( position ).getMessageType();
	}

	public class ViewHolderParent extends RecyclerView.ViewHolder {
		public ViewHolderParent( View itemView ) {
			super( itemView );
		}
	}

	public class ViewHolderText extends ViewHolderParent {
		private RelativeLayout     relativeLayoutRootChatText;
		private ConstraintLayout linearLayoutChatBubble;
		private AppCompatImageView imageViewLeftArrowTextChat;
		private AppCompatImageView imageViewRightArrowTextChat;
		private AppCompatTextView  textViewUserNameChatText;
		private EmojiconTextView textViewUserMessageChatText;
		private AppCompatTextView textViewUserTimeChatText;

		public ViewHolderText( View itemView ) {
			super( itemView );
			relativeLayoutRootChatText =   itemView.findViewById( R.id.relativeLayoutRootChatText );
			linearLayoutChatBubble =   itemView.findViewById( R.id.linearLayoutChatBubble );
			imageViewLeftArrowTextChat = itemView.findViewById( R.id.imageViewLeftArrowTextChat );
			imageViewRightArrowTextChat =   itemView.findViewById( R.id.imageViewRightArrowTextChat );
			textViewUserNameChatText =   itemView.findViewById( R.id.textViewUserNameChatText );
			textViewUserMessageChatText =  itemView.findViewById( R.id.textViewUserMessageChatText );
			textViewUserTimeChatText =   itemView.findViewById( R.id.textViewUserTimeChatText );
		}
	}

	public class ViewHolderImage extends ViewHolderParent {
		private RelativeLayout     relativeLayoutRootChatImage;
		private AppCompatImageView imageViewLeftArrowChatImage;
		private AppCompatImageView imageViewRightArrowChatImage;
		private AppCompatTextView  textViewUserNameChatImage;
		private EmojiconTextView   textViewUserMessageChatImage;
		private RelativeLayout     relativeLayoutImageChat;
		private AppCompatImageView imageViewChatImage;
		private AppCompatImageView imageViewDownloadOrCancelImage;
		private ProgressBar progressBarDownloadPercentageImage;
		private AppCompatTextView textViewUserTimeChatImage;

		public ViewHolderImage( View itemView ) {
			super( itemView );
			relativeLayoutRootChatImage = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutRootChatImage );
			relativeLayoutImageChat = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutImageChat );
			imageViewLeftArrowChatImage = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewLeftArrowChatImage );
			imageViewRightArrowChatImage = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewRightArrowChatImage );
			textViewUserNameChatImage = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserNameChatImage );
			textViewUserMessageChatImage = ( EmojiconTextView ) itemView.findViewById( R.id.textViewUserMessageChatImage );
			imageViewChatImage = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewChatImage );
			imageViewDownloadOrCancelImage = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewDownloadOrCancelImage );
			progressBarDownloadPercentageImage = ( ProgressBar ) itemView.findViewById( R.id.progressBarDownloadPercentageImage );
			textViewUserTimeChatImage = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserTimeChatImage );
		}
	}
	public class ViewHolderVideo extends ViewHolderParent {
		private RelativeLayout     relativeLayoutRootChatVideo;
		private AppCompatImageView imageViewLeftArrowChatVideo;
		private AppCompatImageView imageViewRightArrowChatVideo;
		private AppCompatTextView  textViewUserNameChatVideo;
		private EmojiconTextView   textViewUserMessageChatVideo;
		private RelativeLayout     relativeLayoutVideoChat;
		private AppCompatImageView imageViewChatVideo;
		private AppCompatImageView imageViewDownloadOrCancelVideo;
		private ProgressBar progressBarDownloadPercentageVideo;
		private AppCompatTextView textViewUserTimeChatVideo;

		public ViewHolderVideo( View itemView ) {
			super( itemView );
			relativeLayoutRootChatVideo = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutRootChatVideo );
			relativeLayoutVideoChat = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutVideoChat );
			imageViewLeftArrowChatVideo = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewLeftArrowChatVideo );
			imageViewRightArrowChatVideo = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewRightArrowChatVideo );
			textViewUserNameChatVideo = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserNameChatVideo );
			textViewUserMessageChatVideo = ( EmojiconTextView ) itemView.findViewById( R.id.textViewUserMessageChatVideo );
			imageViewChatVideo = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewChatVideo );
			imageViewDownloadOrCancelVideo = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewDownloadOrCancelVideo );
			progressBarDownloadPercentageVideo = ( ProgressBar ) itemView.findViewById( R.id.progressBarDownloadPercentageVideo );
			textViewUserTimeChatVideo = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserTimeChatVideo );
		}
	}

	public class ViewHolderDate extends ViewHolderParent {
		private LinearLayout       linearLayoutRootChatDate;
		private AppCompatTextView  textViewChatDate;
		public ViewHolderDate( View itemView ) {
			super( itemView );
			linearLayoutRootChatDate = ( LinearLayout ) itemView.findViewById( R.id.linearLayoutRootChatDate );
			textViewChatDate = ( AppCompatTextView ) itemView.findViewById( R.id.textViewChatDate );
		}
	}
	private void loadImageFromSdcard(String path,AppCompatImageView imageview){
		Log.i( TAG, "loadImageFromSdcard: "+path );
		/*File imgFile = new  File( path);
		if(imgFile.exists()){
			Bitmap myBitmap = BitmapFactory.decodeFile( imgFile.getAbsolutePath());

			imageview.setImageBitmap(myBitmap);
		}*/

		Picasso.with(context)
				.load(new File( path ))
				.into(imageview);

	}
	private String getTimeFromDate(String dateTime){
		String time="";
		if(dateTime.length() > 11)
		 time = dateTime.substring( 11, dateTime.length() );
		return time;
	}


	public void updateMessageList(List<MessageModel> employees) {
		final ChatMessageDiffCallback diffCallback = new ChatMessageDiffCallback(this.messageList, employees);
		final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

		this.messageList.clear();
		this.messageList.addAll(employees);
		diffResult.dispatchUpdatesTo(this);
	}
	
}
