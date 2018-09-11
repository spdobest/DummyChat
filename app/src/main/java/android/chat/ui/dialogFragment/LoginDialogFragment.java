package android.chat.ui.dialogFragment;

import android.chat.R;
import android.chat.data.PreferenceManager;
import android.chat.listeners.OnFirebasseActionListener;
import android.chat.ui.activity.HomeActivity;
import android.chat.ui.base.BaseDialogFragment;
import android.chat.ui.base.BaseLoginRegisterDialogFragment;
import android.chat.util.CommonUtils;
import android.chat.util.FirebaseUtility;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class LoginDialogFragment extends BaseLoginRegisterDialogFragment implements View.OnClickListener,OnFirebasseActionListener {


    private static final String TAG = "LoginDialogFragment";

    private FirebaseAuth auth;
    private SharedPreferences prefs;


    private AppCompatEditText edittextEmailId;
    private TextInputLayout textInputLayoutEmail;
    private AppCompatEditText edittextPassword;
    private TextInputLayout textInputLayoutPassword;
    private RadioButton radioStudent;
    private RadioButton radioTeacher;
    private RadioGroup radioGroupStudentTeacher;
    private AppCompatButton buttonLogin;
    private AppCompatTextView textViewDonthaveAccount;
    private AppCompatTextView textViewSkip;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setLayout(R.layout.dialogfragment_login);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setListeners();
    }

    @Override
    protected void init(View rootview) {
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
         edittextEmailId = rootview.findViewById(R.id.edittextEmailId);
         textInputLayoutEmail= rootview.findViewById(R.id.textInputLayoutEmail);
         edittextPassword= rootview.findViewById(R.id.edittextPassword);
         textInputLayoutPassword= rootview.findViewById(R.id.textInputLayoutPassword);
         radioStudent= rootview.findViewById(R.id.radioStudent);
         radioTeacher= rootview.findViewById(R.id.radioTeacher);
         radioGroupStudentTeacher= rootview.findViewById(R.id.radioGroupStudentTeacher);
         buttonLogin= rootview.findViewById(R.id.buttonLogin);
         textViewDonthaveAccount= rootview.findViewById(R.id.textViewDonthaveAccount);
        textViewSkip= rootview.findViewById(R.id.textViewSkip);

         setTitle("Login");
    }

    @Override
    protected void setListeners() {
        buttonLogin.setOnClickListener(this);
        textViewDonthaveAccount.setOnClickListener(this);
        textViewSkip.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin :

                if (!CommonUtils.isInternetAvailable(getActivity())) {
                    showError(getString(R.string.no_internet));
                } else {
                    String email = edittextEmailId.getText().toString().trim();
                    String password = edittextPassword.getText().toString().trim();

                    if (!CommonUtils.isValidEmail(email)) {
                        Toast.makeText(getActivity(), getString(R.string.please_enter_your_valid_email), Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(password)) {
                        Toast.makeText(getActivity(), getString(R.string.please_enter_your_password), Toast.LENGTH_LONG).show();
                    }
                    else {
                        showProgressBar();
                        //authenticate user
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hideProgressBar();
                                        if (!task.isSuccessful()) {
                                            showError("Invalid User name  or Password");
                                        } else {
                                            String userId = task.getResult().getUser().getUid();
                                            if (!TextUtils.isEmpty(userId)) {

                                                if(radioStudent.isChecked()){
                                                    PreferenceManager.getInstance(getActivity()).setIsStudent(true);
                                                }
                                                else{
                                                    PreferenceManager.getInstance(getActivity()).setIsStudent(false);
                                                }
                                                saveData(userId);
                                            }
                                        }
                                    }
                                });
                    }
                }
                break;
            case R.id.textViewDonthaveAccount :
                new RegisterDialogFragment().show(getChildFragmentManager(),"");
                break;
                case R.id.textViewSkip :
               getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
                break;
        }
    }

    private void saveData(String userId){
        FirebaseUtility.saveProfileData(getActivity(),userId,this);
    }

    @Override
    public void onSuccess(String message) {
        getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
        getActivity().finish();
    }

    @Override
    public void onFail(String message) {
        showError(message);
    }
}
