package android.chat.ui.fragments;


import android.chat.R;
import android.chat.adapter.UserAdapter;
import android.chat.data.DatabaseHelper;
import android.chat.data.PreferenceManager;
import android.chat.room.entity.User;
import android.chat.util.CommonUtils;
import android.chat.util.Constants;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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


public class UserListFragment extends Fragment {
    public static final String TAG = "UserListFragment";
    List<User> listUser = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerViewUser;
    private LinearLayout linearLayoutRootUserTab;
    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseChatReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private UserAdapter userAdapter;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseHelper databaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseHelper = new DatabaseHelper(getActivity());
        databaseReference = firebaseDatabase.getReference(Constants.FirebaseConstants.TABLE_USER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // setLayout(R.layout.fragment_user_tab);
     rootView = inflater.inflate(R.layout.fragment_user_tab, container, false);
        recyclerViewUser = (RecyclerView) rootView.findViewById(R.id.recyclerViewUser);
        linearLayoutRootUserTab = (LinearLayout) rootView.findViewById(R.id.linearLayoutRootUserTab);
        recyclerViewUser.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));


        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
               init();
    }


    protected void init() {
        if (PreferenceManager.getInstance(getActivity()).getIsUserExist()) {
            listUser = databaseHelper.getUser(getActivity());
            Log.i(TAG, "onCreateView: listUser " + listUser.size());
            userAdapter = new UserAdapter(getActivity(), listUser);
            recyclerViewUser.setAdapter(userAdapter);
//			userAdapter.notifyDataSetChanged();
        }

        if (CommonUtils.isInternetAvailable(getActivity())) {
            getData();
        }
        else{

        }
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
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i(TAG, "onDataChange: ");
                        if (dataSnapshot != null) {

                            for (DataSnapshot innerDataSanpShot : dataSnapshot.getChildren()) {
                                //DataSnapshot of inner Childerns
                                User modelChat = innerDataSanpShot.getValue(User.class);
                            }


                            Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                            Iterator<DataSnapshot> iterator = dataSnapshotIterable.iterator();

                            while (iterator.hasNext()) {

                                User modelChat = iterator.next().getValue(User.class);
                                Log.i(TAG, "onDataChange: 2 " + modelChat.getEmailId() + " name " + modelChat.getUserName() + " id " + modelChat.getUserId());
                                listUser.add(modelChat);
                                databaseHelper.insertUser(getActivity(), modelChat);
                            }
                            if (userAdapter != null)
                                userAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
}