package android.chat.ui.base.baseInterface;

/**
 * Created by sibaprasad on 19/11/16.
 */

public interface OnProgressAndErrorListener {
	public void showProgressDialog(String message);
	public void hideProgressDialog();
	public void showLoadmoreProgress(String message);
	public void showToolbarProgress();

	public void showError(String message, int errorType);
	public void showSnackbar(String message, int duration);
}
