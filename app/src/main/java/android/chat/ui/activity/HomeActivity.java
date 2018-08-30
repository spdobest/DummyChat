package android.chat.ui.activity;

import android.chat.R;
import android.chat.ui.fragments.UserListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportFragmentManager().beginTransaction().add(R.id.framContainer, new UserListFragment(),"Userlist").commit();

    }
}
