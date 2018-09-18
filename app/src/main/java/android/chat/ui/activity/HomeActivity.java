package android.chat.ui.activity;
/**
 * Created by sibaprasad on 22/12/16.
 */

import android.chat.R;
import android.chat.SpalshActivity;
import android.chat.adapter.HomePagerAdapter;
import android.chat.application.ChatApplication;
import android.chat.background.FirebaseMessageReadJobService;
import android.chat.background.MessageReadingService;
import android.chat.data.PreferenceManager;
import android.chat.ui.fragments.HomeTabFragment;
import android.chat.util.ApplicationUtils;
import android.chat.util.Constants;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * TODO
 */

public class HomeActivity extends AppCompatActivity {

    public static final String TAG = "HomeActivity";


    HomePagerAdapter adapter;

    // widget declaration
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private MessageReadingService m_service;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        tabLayout = ( TabLayout ) findViewById( R.id.tabs );
        viewPager = ( ViewPager ) findViewById( R.id.viewpager );

        setSupportActionBar( toolbar );

        final ActionBar ab = getSupportActionBar();

        startService(new Intent(this, MessageReadingService.class));


        /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            new FirebaseMessageReadJobService().scheduleRefreshForNaught(HomeActivity.this);
        }
        else {*/
            new FirebaseMessageReadJobService().scheduleJobFirebaseToReadMessage(HomeActivity.this);
       // }

        if ( viewPager != null ) {
            setupViewPager( viewPager );
        }

        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
            }
        } );

    }

    private void setupViewPager( ViewPager viewPager ) {
        adapter = new HomePagerAdapter( getSupportFragmentManager() );

        HomeTabFragment homeTabFragmentContacts = new HomeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putShort(Constants.BundleKeys.TAB_TYPE,Constants.TAB_CONTACTS);
        homeTabFragmentContacts.setArguments(bundle);

        HomeTabFragment homeTabFragmentChat = new HomeTabFragment();
        Bundle bundleTeacher = new Bundle();
        bundleTeacher.putShort(Constants.BundleKeys.TAB_TYPE,Constants.TAB_GROUP);
        homeTabFragmentChat.setArguments(bundleTeacher);

        adapter.addFragment(homeTabFragmentContacts , Constants.TAB_NAME_CONTACT );
        adapter.addFragment( homeTabFragmentChat, Constants.TAB_NAME_CHAT  );

        viewPager.setAdapter( adapter );

        setupTabLayout(viewPager);
    }



    private void setupTabLayout(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        int length = tabLayout.getTabCount();
        for (int i = 0; i < length; i++) {
 			tabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
    }
    public View getTabView(int position) {
        View               view   = LayoutInflater.from( this).inflate( R.layout.custom_tab_layout, null);
        AppCompatTextView  title  = view.findViewById( R.id.textViewTabname);
        AppCompatImageView icon   = view.findViewById( R.id.imageViewTab);
        switch (position){
            case 0 :
                title.setText(Constants.TAB_NAME_CONTACT);
                icon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_user));
                break;
            case 1 :
                title.setText(Constants.TAB_NAME_CHAT);
                icon.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_people_grey));
                break;
        }
        return view;
    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.home_menu, menu );
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.menu_share:

                return true;
            case R.id.menu_logout:
                PreferenceManager.getInstance(this).setUserLoggedIN(false);
                ApplicationUtils.clearApplicationData(this);
                startActivity(new Intent(this,SpalshActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            m_service = ((MessageReadingService.MyBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            m_service = null;
        }
    };

}