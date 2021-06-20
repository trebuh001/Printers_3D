package com.example.printers_3d
import android.content.Context
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    val TAG = "Login TAG"
    lateinit var appSettingsPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        appSettingsPrefs = getSharedPreferences("AppSettingPrefs", Context.MODE_PRIVATE)
    }


    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    fun LoginButton(view: View) {
        val email: String = Et_email_login.text?.trim().toString()
        val password: String = Et_password_login.text?.trim().toString()
        val check_fields: Boolean = checkFields(email, password)
        if (check_fields) {
            return
        } else {
            Progress_bar_login.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val intent = Intent(this, UserSettingsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    Log.d(TAG, "login:success")
                    Toast.makeText(this, getString(R.string.toast_login_success), Toast.LENGTH_LONG)
                        .show()
                    Progress_bar_login.visibility = View.GONE
                    finish()

                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.invalid_email_password),
                        Toast.LENGTH_LONG
                    ).show()
                    Progress_bar_login.visibility = View.GONE
                }
            }
        }
        Progress_bar_login.visibility = View.GONE

    }

    fun ForgotButton(view: View) {
        val intent = Intent(this, RemindPasswordActivity::class.java)
        startActivity(intent)
    }

    fun ShowPassword(view: View) {
        if (view.getId() == R.id.Btn_show_pass) {

            if (Et_password_login.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) {
                Btn_show_pass.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_password_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                Btn_show_pass.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }






    fun checkFields(email: String, password: String):Boolean
    {
        var i:Int=0
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Et_email_login.setError(getString(R.string.email_not_valid))
            Et_email_login.requestFocus()
            i++
        }
        if(email.trim().isEmpty())
        {
            Et_email_login.setError(getString(R.string.field_cant_be_empty))
            Et_email_login.requestFocus()
            i++

        }
        if(password.trim().isEmpty())
        {
            Et_password_login.setError(getString(R.string.field_cant_be_empty))
            Et_password_login.requestFocus()
            i++
        }
        if(password.trim().length<6)
        {
            Et_password_login.setError(getString(R.string.password_shorter_than_6_char))
            Et_password_login.requestFocus()
            i++

        }
        return i>0
    }


}