package android.chat

import android.chat.ui.dialogFragment.LoginDialogFragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp

class SpalshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        FirebaseApp.initializeApp(this)

        var login:LoginDialogFragment = LoginDialogFragment()

        login.show(supportFragmentManager,"LOgin")
    }
}
