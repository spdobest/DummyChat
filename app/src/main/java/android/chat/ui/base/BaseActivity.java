package android.chat.ui.base;

import android.app.Fragment;
import android.chat.ui.base.baseInterface.OnProgressAndErrorListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by sibaprasad on 19/11/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements OnProgressAndErrorListener {

	public abstract void initializeView();
	public abstract void setOnCLickListener();


	@Override
	protected void onCreate( @Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onAttachFragment( Fragment fragment ) {
		super.onAttachFragment( fragment );
	}


	@Override
	public void showProgressDialog( String message ) {

	}

	@Override
	public void hideProgressDialog() {

	}

	@Override
	public void showLoadmoreProgress( String message ) {

	}

	@Override
	public void showToolbarProgress() {

	}

	@Override
	public void showError( String message, int errorType ) {

	}

	@Override
	public void showSnackbar( String message, int duration ) {

	}
}
