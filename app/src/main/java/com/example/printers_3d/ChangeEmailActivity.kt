package com.example.printers_3d

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_change_email.*
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangeEmailActivity : AppCompatActivity() {
    val TAG="ChangeEmail TAG"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
    }
    fun ChangeEmailButton(view: View)
    {
        val old_email: String = Et_old_email_change_email.text?.trim().toString()
        val password: String = Et_password_change_email.text?.trim().toString()
        val new_email: String = Et_new_email_change_email.text?.trim().toString()
        val new_email_repeat: String = Et_new_email_change_email_repeat.text?.trim().toString()
        val check_fields: Boolean = CheckFields(old_email, password, new_email,new_email_repeat)
        if (check_fields) {
            return
        }
        if (new_email.equals(new_email_repeat))
        {
            val user = mAuth.currentUser!!
// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
            val credential = EmailAuthProvider
                .getCredential(old_email, password)
// Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User re-authenticated.")
                        Toast.makeText(this,getString(R.string.auth_successful),Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Log.d(TAG, "User re-authenticated.")
                        Toast.makeText(this,getString(R.string.auth_error),Toast.LENGTH_SHORT).show()

                    }
                }
            user.updateEmail(new_email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().uid.toString()).child("email").
                            setValue(new_email)
                            .addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    Log.d(TAG, "E-mail in profile updated successfully.")
                                    Toast.makeText(this, getString(R.string.email_profile_changed_success), Toast.LENGTH_SHORT).show()
                                }

                                else
                                {
                                    Log.d(TAG, "User re-authenticated.")
                                    Toast.makeText(this,getString(R.string.email_profile_changed_error),Toast.LENGTH_SHORT).show()

                                }
                            }
                        Log.d(TAG, "E-mail in profile error updated.")
                        Toast.makeText(this,getString(R.string.email_changed_success),Toast.LENGTH_LONG).show()

                        Et_old_email_change_email.getText().clear()
                        Et_password_change_email.getText().clear()
                        Et_new_email_change_email.getText().clear()
                        Et_new_email_change_email_repeat.getText().clear()
                    }
                    else
                    {
                        Log.d(TAG, "User email address updated error.")
                        Toast.makeText(this,getString(R.string.email_changed_error),Toast.LENGTH_LONG).show()
                    }

                }


        }
        else
        {
            Log.d(TAG, "User email updated error.")
            Toast.makeText(this,getString(R.string.password_changed_error), Toast.LENGTH_LONG).show()
        }

    }

    fun ShowPassword(view: View)
    {
        if (view.getId() == R.id.Btn_show_pass_change_email) {
            if (Et_password_change_email.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance()
                )
            ) {
                Btn_show_pass_change_email.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_password_change_email.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                Btn_show_pass_change_email.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_password_change_email.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    fun CheckFields(old_email: String, password: String, new_email: String,new_email_repeat: String):Boolean
    {
        var i:Int=0;

        if(old_email.trim().isEmpty())
        {
            Et_old_email_change_email.setError(getString(R.string.field_cant_be_empty))
            Et_old_email_change_email.requestFocus()
            i++
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(old_email).matches())
        {
            Et_old_email_change_email.setError(getString(R.string.email_not_valid))
            Et_old_email_change_email.requestFocus()
            i++
        }
        if(new_email.trim().isEmpty())
        {
            Et_new_email_change_email.setError(getString(R.string.field_cant_be_empty))
            Et_new_email_change_email.requestFocus()
            i++
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(new_email).matches())
        {
            Et_new_email_change_email.setError(getString(R.string.email_not_valid))
            Et_new_email_change_email.requestFocus()
            i++
        }

        if(new_email_repeat.trim().isEmpty())
        {
            Et_new_email_change_email_repeat.setError(getString(R.string.field_cant_be_empty))
            Et_new_email_change_email_repeat.requestFocus()
            i++
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(new_email_repeat).matches())
        {
            Et_new_email_change_email_repeat.setError(getString(R.string.email_not_valid))
            Et_new_email_change_email_repeat.requestFocus()
            i++
        }
        if(password.trim().isEmpty()) {
            Et_password_change_email.setError(getString(R.string.field_cant_be_empty))
            Et_password_change_email.requestFocus()
            i++
        }
        if(password.trim().length<6)
        {
            Et_password_change_email.setError(getString(R.string.password_shorter_than_6_char))
            Et_password_change_email.requestFocus()
            i++
        }
        return i>0
    }

    override fun onBackPressed() {
        val intent= Intent(this,UserSettingsActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
        this.finish()
        startActivity(intent)
    }

}