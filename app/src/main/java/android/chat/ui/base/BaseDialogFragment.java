package android.chat.ui.base;

import android.app.Dialog;
import android.chat.R;
import android.chat.util.CommonUtils;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import butterknife.ButterKnife;

public abstract class BaseDialogFragment extends DialogFragment{

    private ProgressBar mProgressBar;
    private AppCompatImageView imageViewBack;
    private AppCompatTextView textViewTitle;
    private int mLayoutId;
    protected String mRequestTag;
    private ConstraintLayout constraintLayoutRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_base, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarDialogFragment);
        constraintLayoutRoot =  view.findViewById(R.id.constraintLayoutRoot);
        imageViewBack = (AppCompatImageView) view.findViewById(R.id.imageViewBack);
        textViewTitle = (AppCompatTextView) view.findViewById(R.id.textViewTitle);
        FrameLayout fragmentLayoutContainer = (FrameLayout) view.findViewById(R.id.frameLayooutDialogFragment);
        inflater.inflate(mLayoutId, fragmentLayoutContainer);

        ButterKnife.bind( this, view );

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        return view;
    }

    protected abstract void init(View rootView);
    protected abstract void setListeners();


    protected void setLayout(int layoutId) {
        mLayoutId = layoutId;
    }


    protected void hideProgressBar() {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
    }

    protected void showProgressBar() {
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.VISIBLE);
    }

    public void showError(String message){
        CommonUtils.showSnackBar(constraintLayoutRoot,message, Snackbar.LENGTH_SHORT);

    }

    protected void setTitle(String title){
        textViewTitle.setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if ( dialog != null ) {
            dialog.getWindow().setWindowAnimations(
                    R.style.styleDialogFragment );
            dialog.getWindow().setLayout( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        }
    }

}
