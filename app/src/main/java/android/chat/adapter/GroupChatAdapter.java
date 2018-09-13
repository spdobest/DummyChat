package android.chat.adapter;

import android.chat.R;
import android.chat.data.PreferenceManager;
import android.chat.listeners.OnActionListener;
import android.chat.room.entity.MessageModel;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * Created by sibaprasad on 24/12/16.
 */

public class GroupChatAdapter extends RecyclerView.Adapter< GroupChatAdapter.ViewHolderParent > {

    public static final String TAG = "GroupChatAdapter";

    public static final int ROW_TYPE_TEXT  = 1;
    public  static final int ROW_TYPE_IMAGE = 2;
    public static final int ROW_TYPE_VIDEO = 3;
    public static final int ROW_TYPE_AUDIO = 4;
    public static final int ROW_TYPE_FILE = 5;

    public static final int ROW_TYPE_DATE = 6;

    LayoutInflater layoutInflater;
    private Context           context;
    private List<MessageModel> listData;
    private String            userId;
    private String            userName;
    private OnActionListener onActionListener;

    public GroupChatAdapter( Context context, List< MessageModel > listData,
                             String userId, OnActionListener mOnActionListener ) {
        this.context = context;
        this.listData = listData;
        layoutInflater = LayoutInflater.from( context );
        this.userId = userId;
        userName = PreferenceManager.getInstance( context ).getUserName();
        this.onActionListener = mOnActionListener;
        Log.i( TAG, "GroupChatAdapter: user id " + userId + " size " + listData.size() );
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
        else if ( viewType == ROW_TYPE_FILE ){
            v = LayoutInflater.from( parent.getContext() )
                    .inflate( R.layout.itemview_doc, parent, false );

            return new ViewHolderDoc( v );
        }
        else{
            v = LayoutInflater.from( parent.getContext() )
                    .inflate( R.layout.itemview_chat_date, parent, false );

            return new ViewHolderDate( v );
        }

		/*ViewHolderParent viewHolderParent = null;
		View             viewHome;
		switch ( viewType ) {
			case ROW_TYPE_TEXT:
				viewHome = LayoutInflater.from( context )
						.inflate( R.layout.itemview_chat_text, parent, false );
				viewHolderParent = new ViewHolderText( viewHome );
				break;
			case ROW_TYPE_IMAGE:
				viewHome = LayoutInflater.from( context )
						.inflate( R.layout.itemview_chat_text, parent, false );
				viewHolderParent = new ViewHolderImage( viewHome );
				break;
		}
		return viewHolderParent;*/
    }

    @Override
    public void onBindViewHolder( ViewHolderParent holder, int position ) {
        final MessageModel modelChat = listData.get( position );
        switch ( holder.getItemViewType() ) {
            case ROW_TYPE_TEXT:
                final ViewHolderText viewHolderText = ( ViewHolderText ) holder;
                viewHolderText.textViewUserNameChatText.setText( modelChat.getSenderName() );
                viewHolderText.textViewUserMessageChatText.setText( modelChat.getMessage() );
                viewHolderText.textViewUserTimeChatText.setText( modelChat.getMessageTime() );

                Log.i( TAG, "onBindViewHolder: "+userId.equalsIgnoreCase( modelChat.getSenderId() ) );

                if ( userId.equalsIgnoreCase(modelChat.getCurrentUserId()) ) {
                    viewHolderText.linearLayoutChatBubble.setBackground(ContextCompat.getDrawable(viewHolderText.relativeLayoutRootChatText.getContext(),
                            R.drawable.drawable_chatbuble_send));
                    viewHolderText.relativeLayoutRootChatText.setGravity( Gravity.RIGHT );
                    viewHolderText.imageViewRightArrowTextChat.setVisibility( View.GONE );
                    viewHolderText.imageViewLeftArrowTextChat.setVisibility( View.GONE );
                }
                else {
                    viewHolderText.relativeLayoutRootChatText.setGravity( Gravity.LEFT );
                    viewHolderText.imageViewLeftArrowTextChat.setVisibility( View.GONE);
                    viewHolderText.imageViewRightArrowTextChat.setVisibility( View.GONE );
                    viewHolderText.linearLayoutChatBubble.setBackground(ContextCompat.getDrawable(viewHolderText.relativeLayoutRootChatText.getContext(),
                            R.drawable.drawable_chatbuble_recieve));
                }

                break;

            case ROW_TYPE_IMAGE:
                final ViewHolderImage viewHolderImage = ( ViewHolderImage ) holder;
                Log.i( TAG, "onBindViewHolder: image PATH  "+modelChat.getPathOrUrl() );
                viewHolderImage.textViewUserNameChatImage.setText( modelChat.getSenderName() );
                viewHolderImage.textViewUserMessageChatImage.setText( modelChat.getMessage() );
                viewHolderImage.textViewUserTimeChatImage.setText( getTimeFromDate( modelChat.getMessageTime() ) );
                if (userId.equalsIgnoreCase(modelChat.getCurrentUserId())) {
                    viewHolderImage.relativeLayoutRootChatImage.setGravity( Gravity.RIGHT );
                    viewHolderImage.imageViewRightArrowChatImage.setVisibility( View.VISIBLE );
                    viewHolderImage.imageViewLeftArrowChatImage.setVisibility( View.GONE );
                }
                else {
                    viewHolderImage.relativeLayoutRootChatImage.setGravity( Gravity.LEFT );
                    viewHolderImage.imageViewLeftArrowChatImage.setVisibility( View.VISIBLE );
                    viewHolderImage.imageViewRightArrowChatImage.setVisibility( View.GONE );
                }
                if(modelChat.isDownloaded == 0)
                    Picasso.with(context)
                            .load(modelChat.getPathOrUrl())
                            .into(viewHolderImage.imageViewChatImage);
                else
                    loadImageFromSdcard(modelChat.getPathOrUrl(),viewHolderImage.imageViewChatImage);
                viewHolderImage.imageViewChatImage.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View view ) {
                        // ImageVideoDialogFragment.newInstance( false,ImageVideoDialogFragment.FILE_TYPE_IMAGE, modelChat.PathOrUrl ).show( (( AppCompatActivity)context).getSupportFragmentManager(), ImageVideoDialogFragment.TAG );
                    }
                } );


                break;
            case ROW_TYPE_VIDEO:
                final ViewHolderVideo viewHolderVideo = ( ViewHolderVideo ) holder;
                viewHolderVideo.textViewUserNameChatVideo.setText( modelChat.getSenderName() );
                viewHolderVideo.textViewUserMessageChatVideo.setText( modelChat.getMessage() );
                if ( userId.equalsIgnoreCase(modelChat.getCurrentUserId())  ) {
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
                        .load(modelChat.getPathOrUrl())
                        .into(viewHolderVideo.imageViewChatVideo);
                break;
            case ROW_TYPE_FILE :
                final ViewHolderDoc viewHolderDoc = (ViewHolderDoc) holder;
                viewHolderDoc.textViewUserTimeChatVideo.setText( modelChat.getMessageTime());

                if(modelChat.isTeacher == 1) {
                    if (modelChat.getIsAccepted() == 1) {
                        viewHolderDoc.buttonApprove.setText("Approved");
                    } else {
                        viewHolderDoc.buttonApprove.setText("Approve");
                    }
                }
                else{
                    if (modelChat.getIsAccepted() == 1) {
                        viewHolderDoc.buttonApprove.setText("Approved");
                    } else {
                        viewHolderDoc.buttonApprove.setText("Waiting to approve");
                    }
                }
                viewHolderDoc.imageViewDownloadOrCancelVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(modelChat.getIsTeacher()==0) {
                            if (modelChat.getIsAccepted() == 1) {
                                onActionListener.onDownloadClick(modelChat);
                            } else {
                                Toast.makeText(context, "File not Approved By teacher", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                viewHolderDoc.buttonApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(modelChat.getIsAccepted() == 1 && modelChat.getIsTeacher() == 1) {
                            onActionListener.onApproveClick(modelChat);
                        }
                    }
                });
                break;
            case ROW_TYPE_DATE :
                final ViewHolderDate viewHolderDate = (ViewHolderDate) holder;
                viewHolderDate.textViewChatDate.setText( modelChat.getMessageDate() );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType( int position ) {
        return listData.get( position ).messageType;
    }

    public class ViewHolderParent extends RecyclerView.ViewHolder {
        public ViewHolderParent( View itemView ) {
            super( itemView );
        }
    }

    public class ViewHolderText extends ViewHolderParent {
        private RelativeLayout     relativeLayoutRootChatText;
        private LinearLayout     linearLayoutChatBubble;
        private AppCompatImageView imageViewLeftArrowTextChat;
        private AppCompatImageView imageViewRightArrowTextChat;
        private AppCompatTextView  textViewUserNameChatText;
        private EmojiconTextView textViewUserMessageChatText;
        private AppCompatTextView textViewUserTimeChatText;

        public ViewHolderText( View itemView ) {
            super( itemView );
            relativeLayoutRootChatText = ( RelativeLayout ) itemView.findViewById( R.id.relativeLayoutRootChatText );
            linearLayoutChatBubble = ( LinearLayout ) itemView.findViewById( R.id.linearLayoutChatBubble );
            imageViewLeftArrowTextChat = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewLeftArrowTextChat );
            imageViewRightArrowTextChat = ( AppCompatImageView ) itemView.findViewById( R.id.imageViewRightArrowTextChat );
            textViewUserNameChatText = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserNameChatText );
            textViewUserMessageChatText = ( EmojiconTextView ) itemView.findViewById( R.id.textViewUserMessageChatText );
            textViewUserTimeChatText = ( AppCompatTextView ) itemView.findViewById( R.id.textViewUserTimeChatText );
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
    public class ViewHolderDoc extends ViewHolderParent {
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
        private AppCompatButton buttonApprove;

        public ViewHolderDoc( View itemView ) {
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
            buttonApprove = itemView.findViewById( R.id.buttonApprove );
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
}
