package android.chat.ui.base.baseInterface;

/**
 * Created by sibaprasad on 19/11/16.
 */

public interface OnRecyclerItemClicklistener {
	public void onRecyclerItemClick(int position);
	public void onRecyclerItemClickWithData(int position, String title, String message);
}
