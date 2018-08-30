package android.chat.ui.base;

import android.chat.R;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

abstract public class RecyclerViewFragment extends Fragment {


    private ProgressBar mProgressBar;
    private int mLayoutId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_base, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbarFragment);
        FrameLayout fragmentLayoutContainer = (FrameLayout) view.findViewById(R.id.frameLayooutDialogFragment);
        inflater.inflate(mLayoutId, fragmentLayoutContainer);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    abstract void init(View rootView);
    abstract void setOnClickListener();

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


}
