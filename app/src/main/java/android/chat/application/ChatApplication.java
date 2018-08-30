package android.chat.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class ChatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
