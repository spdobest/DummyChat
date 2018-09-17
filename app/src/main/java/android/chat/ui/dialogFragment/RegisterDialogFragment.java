package android.chat.ui.dialogFragment;

import android.chat.R;
import android.chat.data.PreferenceManager;
import android.chat.model.teacher.TeacherData;
import android.chat.room.entity.UserOrGroupDetails;
import android.chat.ui.activity.HomeActivity;
import android.chat.ui.base.BaseLoginRegisterDialogFragment;
import android.chat.util.ApplicationUtils;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDialogFragment extends BaseLoginRegisterDialogFragment implements View.OnClickListener {

    private static final String TAG = "RegisterDialogFragment";
    private SharedPreferences prefs;
    private AppCompatEditText edittextName;
    private TextInputLayout textInputLayoutName;
    private AppCompatEditText edittextEmailId;
    private AppCompatEditText edittextMobile;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutMobile;
    private AppCompatEditText edittextPassword;
    private TextInputLayout textInputLayoutPassword;
    private RadioButton radioStudent;
    private RadioButton radioTeacher;
    private RadioGroup radioGroupStudentTeacher;
    private AppCompatTextView textViewSelectedSubjects;
    private AppCompatButton buttonRegister;
    private AppCompatTextView textViewHaveAccount;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private String loginType;
    private String selectedSubject;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setLayout(R.layout.dialogfragment_register);
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
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edittextName = rootview.findViewById(R.id.edittextName);
        textInputLayoutName = rootview.findViewById(R.id.textInputLayoutName);
        edittextEmailId = rootview.findViewById(R.id.edittextEmailId);
        edittextMobile = rootview.findViewById(R.id.edittextMobile);
        textInputLayoutEmail = rootview.findViewById(R.id.textInputLayoutEmail);
        textInputLayoutMobile = rootview.findViewById(R.id.textInputLayoutMobile);
        edittextPassword = rootview.findViewById(R.id.edittextPassword);
        textInputLayoutPassword = rootview.findViewById(R.id.textInputLayoutPassword);
        radioStudent = rootview.findViewById(R.id.radioStudent);
        radioTeacher = rootview.findViewById(R.id.radioTeacher);
        radioGroupStudentTeacher = rootview.findViewById(R.id.radioGroupStudentTeacher);
        textViewSelectedSubjects = rootview.findViewById(R.id.textViewSelectedSubjects);
        buttonRegister = rootview.findViewById(R.id.buttonRegister);
        textViewHaveAccount = rootview.findViewById(R.id.textViewHaveAccount);

        setTitle("Register");
    }

    @Override
    protected void setListeners() {
        buttonRegister.setOnClickListener(this);
        textViewHaveAccount.setOnClickListener(this);
        textViewSelectedSubjects.setOnClickListener(this);
        radioGroupStudentTeacher.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == 0) {
                    loginType = getString(R.string.student);
                } else {
                    loginType = getString(R.string.teacher);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
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
                    else if(getSelectedSubjectList().size() == 0){
                        showError("Please Select atleast one Subject");
                        showSubjectBottomsheet();
                    }
                    /* else if (TextUtils.isEmpty(loginType)) {
                        Toast.makeText(getActivity(), getString(R.string.please_select_studentorteacher), Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(selectedSubject)) {
                        Toast.makeText(getActivity(), getString(R.string.please_select_subject), Toast.LENGTH_LONG).show();
                    } */else {
                        showProgressBar();
                        //creating a new user
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //checking if success
                                        if (task.isSuccessful()) {
                                            onAuthSuccess(task.getResult().getUser());
                                        } else {
                                            //display some message here
                                            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        hideProgressBar();
                                    }
                                });
                    }
                }


                break;
            case R.id.textViewHaveAccount:
                new LoginDialogFragment().show(getChildFragmentManager(), "Login");
                break;
            case R.id.textViewSelectedSubjects :
                showSubjectBottomsheet();
                break;
        }
    }

    private void onAuthSuccess(FirebaseUser user) {

        if(user !=null) {
            String userId = user.getUid();
            String name = edittextName.getText().toString().trim();
            String email= edittextName.getText().toString().trim();
            String number = edittextMobile.getText().toString().trim();

            PreferenceManager.getInstance(getActivity()).setUserLoggedIN(true);

            PreferenceManager.getInstance(getActivity()).setSubjectList(ApplicationUtils.getStringWithComma(getSelectedSubjectList()));
            PreferenceManager.getInstance(getActivity()).setUserId(userId);

            String subjectList = ApplicationUtils.getStringWithComma(getSelectedSubjectList());
            TeacherData teacherData = new TeacherData(userId,
                    name,
                    email,
                    number,
                    subjectList,
                    edittextPassword.getText().toString().trim()
            );

            /**
             * String name, String email, String userid, String number,
             String subjectList, int isGroup, int isTeacher
             */

            if (radioStudent.isChecked()) {
                UserOrGroupDetails userOrGroupDetails = new UserOrGroupDetails(name,email,userId,number,subjectList,0,0);

                mDatabase.child(Constants.FirebaseConstants.TABLE_STUDENT).child(userId).setValue(userOrGroupDetails);
                mDatabase.child(Constants.FirebaseConstants.TABLE_USER).child(userId).setValue(userOrGroupDetails);
                PreferenceManager.getInstance(getActivity()).setIsStudent(true);
            } else {
                UserOrGroupDetails userOrGroupDetails = new UserOrGroupDetails(name,email,userId,number,subjectList,0,1);

                mDatabase.child(Constants.FirebaseConstants.TABLE_TEACHER).child(userId).setValue(userOrGroupDetails);
                PreferenceManager.getInstance(getActivity()).setIsStudent(false);

                mDatabase.child(Constants.FirebaseConstants.TABLE_USER).child(userId).setValue(userOrGroupDetails);
            }
            getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));
            PreferenceManager.getInstance(getActivity()).setUserName(name);
        }
        else{
            showError("Error");
        }
    }

    private void writeNewUser(String userId, String name, String email, String number) {


    }

}
