package android.chat

import android.chat.data.PreferenceManager
import android.chat.ui.activity.HomeActivity
import android.chat.ui.dialogFragment.LoginDialogFragment
import android.chat.ui.dialogFragment.RegisterDialogFragment
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp

import kotlinx.android.synthetic.main.activity_spalsh.*

class SpalshActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        FirebaseApp.initializeApp(this)

        buttonLogin.setOnClickListener {
            var login: LoginDialogFragment = LoginDialogFragment()
            login.show(supportFragmentManager, "Login")
        }


        buttonRegister.setOnClickListener {
            var register: RegisterDialogFragment = RegisterDialogFragment()
            register.show(supportFragmentManager, "Register")
        }

        if(PreferenceManager.getInstance(this).isUserLoggedIn){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }


    }
}
