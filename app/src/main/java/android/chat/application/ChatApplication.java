package android.chat.application;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatApplication extends Application {

    static Context mContext;
    private static FirebaseDatabase database;
    private static DatabaseReference dbRef ;

    public ChatApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public ChatApplication(Context mContext) {
        this.mContext = mContext;
        database = FirebaseDatabase.getInstance();
    }

    public static Context getContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }
    public static DatabaseReference getFirebaseDatabaseReference() {
        if(dbRef==null) {
            dbRef = FirebaseDatabase.getInstance().getReference();
        }
        return dbRef;
    }
}
