package com.example.printers_3d

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_remind_password.*
import java.util.*

class RemindPasswordActivity : AppCompatActivity() {
    val TAG="RemindPassword TAG"
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remind_password)

    }
    fun RemindButton(view: View)
    {

        val email:String= Et_email_remind.text?.trim().toString()
        val check_fields:Boolean=CheckFields(email)
        if(check_fields)
        {
            return

        }
        Progress_bar_remind.visibility= View.VISIBLE

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        if(Locale.getDefault().getLanguage()=="pl")
        {
            mAuth.setLanguageCode("pl")
        }
        else
        {
            mAuth.setLanguageCode("en")
        }
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,getString(R.string.toast_remind_password),
                        Toast.LENGTH_LONG).show()
                    Et_email_remind.getText().clear()
                }
                else
                {
                    Toast.makeText(this,getString(R.string.error_remind_password),
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    fun CheckFields(email: String):Boolean
    {
        var i:Int=0
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Et_email_remind.setError(getString(R.string.email_not_valid))
            Et_email_remind.requestFocus()
            i++
        }
        if(email.trim().isEmpty())
        {
            Et_email_remind.setError(getString(R.string.field_cant_be_empty))
            Et_email_remind.requestFocus()
            i++
        }
        return i>0
    }

}