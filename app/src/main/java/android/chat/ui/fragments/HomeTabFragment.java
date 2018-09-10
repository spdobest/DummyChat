package android.chat.ui.fragments;


import android.chat.R;
import android.chat.adapter.HomeCommonAdapter;
import android.chat.data.DatabaseHelper;
import android.chat.listeners.StudentTeacherOrSubjectListener;
import android.chat.model.teacherSIgnup.SignupModel;
import android.chat.ui.activity.ChatActivity;
import android.chat.ui.activity.SubjectChatActivity;
import android.chat.util.Constants;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class HomeTabFragment extends Fragment implements StudentTeacherOrSubjectListener {
    public static final String TAG = "UserListFragment";



    private View rootView;
    private RecyclerView recyclerViewUser;
    private ConstraintLayout linearLayoutRootUserTab;
    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseChatReferenceStudent;
    private DatabaseReference databaseReferenceTeacher;
    private FirebaseDatabase firebaseDatabase;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseHelper databaseHelper;



    // data for adapter
    private List<SignupModel>   listStudents= new ArrayList<>();
    private List<SignupModel>   listTeachers = new ArrayList<>();
    private List<String>        listSubject = new ArrayList<>();
    private HomeCommonAdapter homeCommonAdapter;

    private short tab_type = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseHelper = new DatabaseHelper(getActivity());

        databaseChatReferenceStudent = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_STUDENT);
        databaseReferenceTeacher = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_TEACHER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // setLayout(R.layout.fragment_user_tab);
     rootView = inflater.inflate(R.layout.fragment_user_tab, container, false);
        recyclerViewUser = (RecyclerView) rootView.findViewById(R.id.recyclerViewUser);
        linearLayoutRootUserTab = (ConstraintLayout) rootView.findViewById(R.id.linearLayoutRootUserTab);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

               Bundle bundle = getArguments();
               if(bundle!=null){
                   if(bundle.containsKey(Constants.BundleKeys.TAB_TYPE)){
                       tab_type = bundle.getShort(Constants.BundleKeys.TAB_TYPE);
                   }
               }


               recyclerViewUser.setLayoutManager(new LinearLayoutManager(getActivity()));

               getData();

    }


    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    private void getData() {
        //	databaseChatReference = FirebaseDatabase.getInstance().getReference().child( Constants.FirebaseConstants.TABLE_CHAT );

        switch (tab_type){

            case Constants.TAB_SUBJECT :
                if(listSubject.size()==0) {
                    listSubject.add("Android");
                    listSubject.add("Iphone");
                    listSubject.add("Java");
                    listSubject.add("Python");
                }

                    homeCommonAdapter = new HomeCommonAdapter(getActivity(),Constants.TAB_SUBJECT,this);
                    homeCommonAdapter.setSubjectData(listSubject);
                    recyclerViewUser.setAdapter(homeCommonAdapter);


                break;
                case Constants.TAB_TEACHER:
                   setupTeacherList();
                    break;
            case Constants.TAB_STUDENT :
                setUpStudentList();
                break;

        }
    }


    private void setupTeacherList(){

            homeCommonAdapter = new HomeCommonAdapter(getActivity(),Constants.TAB_TEACHER,this);
            homeCommonAdapter.setStudentOrData(listTeachers);
            recyclerViewUser.setAdapter(homeCommonAdapter);



        if(listTeachers.size()==0) {
            databaseReferenceTeacher.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(TAG, "onDataChange: ");
                            if (dataSnapshot != null) {

                           /* for (DataSnapshot innerDataSanpShot : dataSnapshot.getChildren()) {
                                //DataSnapshot of inner Childerns
                                SignupModel signupModel = innerDataSanpShot.getValue(SignupModel.class);
                            }*/

                                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                                Iterator<DataSnapshot> iterator = dataSnapshotIterable.iterator();

                                while (iterator.hasNext()) {
                                    SignupModel signupModel = iterator.next().getValue(SignupModel.class);
                                    Log.i(TAG, "onDataChange: 2 " + signupModel.getEmail() + " name " + signupModel.getName());
                                    listTeachers.add(signupModel);
                                    //  databaseHelper.insertUser(getActivity(), signupModel);
                                }
                                homeCommonAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
        else{
            homeCommonAdapter.setStudentOrData(listTeachers);
            homeCommonAdapter.notifyDataSetChanged();
        }

    }

    private void setUpStudentList(){


        if (homeCommonAdapter == null){
            homeCommonAdapter = new HomeCommonAdapter(getActivity(),Constants.TAB_STUDENT,this);
            homeCommonAdapter.setStudentOrData(listStudents);
            recyclerViewUser.setAdapter(homeCommonAdapter);
        }

        if(listStudents.size()==0) {
            databaseChatReferenceStudent.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(TAG, "onDataChange: ");
                            if (dataSnapshot != null) {

                           /* for (DataSnapshot innerDataSanpShot : dataSnapshot.getChildren()) {
                                //DataSnapshot of inner Childerns
                                SignupModel signupModel = innerDataSanpShot.getValue(SignupModel.class);
                            }*/

                                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                                Iterator<DataSnapshot> iterator = dataSnapshotIterable.iterator();

                                while (iterator.hasNext()) {
                                    SignupModel signupModel = iterator.next().getValue(SignupModel.class);
                                    Log.i(TAG, "onDataChange: 2 " + signupModel.getEmail() + " name " + signupModel.getName());
                                    listStudents.add(signupModel);
                                    //  databaseHelper.insertUser(getActivity(), signupModel);
                                }
                                homeCommonAdapter.notifyDataSetChanged();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
        else{
            homeCommonAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSelectStudentOrTeacher(SignupModel signupModel) {
        if(signupModel!=null) {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra(Constants.BundleKeys.USER_NAME, signupModel.getName());
            intent.putExtra(Constants.BundleKeys.USER_ID, signupModel.getUserid());
            startActivity(intent);
        }
    }

    @Override
    public void onSelectSubject(String subjectName) {
        Intent intent  = new Intent(getActivity(), SubjectChatActivity.class);
        startActivity(intent);

    }
}