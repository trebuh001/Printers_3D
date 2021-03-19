package com.example.printers_3d

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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.Btn_show_pass

class ChangePasswordActivity : AppCompatActivity() {
    val TAG="ChangePassword TAG"
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

    }

    fun ChangePasswordButton(view:View) {
        val email: String = Et_email_change_pass.text?.trim().toString()
        val old_password: String = Et_old_password_change_pass.text?.trim().toString()
        val new_password: String = Et_new_password_change_pass.text?.trim().toString()
        val new_password_repeat: String = Et_new_password_change_pass_repeat.text?.trim().toString()
        val check_fields: Boolean = CheckFields(email, old_password, new_password,new_password_repeat)
        if (check_fields) {
            return
        }

        if (new_password.equals(new_password_repeat))
        {
            Progress_bar_change_pass.visibility= View.VISIBLE
            val user = mAuth.currentUser!!
// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
            val credential = EmailAuthProvider
                .getCredential(email, old_password)
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
                        return@addOnCompleteListener

                    }

                }
            user.updatePassword(new_password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                        Toast.makeText(this,getString(R.string.password_changed_success),Toast.LENGTH_LONG).show()
                        Et_email_change_pass.getText().clear()
                        Et_old_password_change_pass.getText().clear()
                        Et_new_password_change_pass.getText().clear()
                        Et_new_password_change_pass_repeat.getText().clear()
                    }
                    else
                    {
                        Log.d(TAG, "User password updated error.")
                        Toast.makeText(this,getString(R.string.password_changed_error),Toast.LENGTH_LONG).show()
                    }
                }
        }
        else
        {
            Et_new_password_change_pass.setError(getString(R.string.passwords_not_same))
            Et_new_password_change_pass_repeat.setError(getString(R.string.passwords_not_same))

            return
        }
        Progress_bar_change_pass.visibility= View.GONE
    }
    fun ShowPassword(view: View)
    {
        if(view.getId()==R.id.Btn_show_pass_old) {
            if(Et_old_password_change_pass.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance()
                )){
                Btn_show_pass_old.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_old_password_change_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                Btn_show_pass_old.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_old_password_change_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        if(view.getId()==R.id.Btn_show_pass_new) {
            if(Et_new_password_change_pass.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance()
                )){
                Btn_show_pass_new.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_new_password_change_pass.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
                );
            }
            else{
                Btn_show_pass_new.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_new_password_change_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        if(view.getId()==R.id.Btn_show_pass_new_repeat) {
            if(Et_new_password_change_pass_repeat.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance()
                )){
                Btn_show_pass_new_repeat.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_new_password_change_pass_repeat.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
                );
            }
            else{
                Btn_show_pass_new_repeat.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_new_password_change_pass_repeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    fun CheckFields(email: String, old_password: String, new_password: String,new_password_repeat: String):Boolean
    {
        var i:Int=0;

        if(email.trim().isEmpty())
        {
            Et_email_change_pass.setError(getString(R.string.field_cant_be_empty))
            Et_email_change_pass.requestFocus()
            i++


        }
        if(old_password.trim().isEmpty())
        {
            Et_old_password_change_pass.setError(getString(R.string.field_cant_be_empty))
            Et_old_password_change_pass.requestFocus()
            i++
        }
        if(new_password.trim().isEmpty())
        {
            Et_new_password_change_pass.setError(getString(R.string.field_cant_be_empty))
            Et_new_password_change_pass.requestFocus()
            i++
        }
        if(new_password_repeat.trim().isEmpty())
        {
            Et_new_password_change_pass_repeat.setError(getString(R.string.field_cant_be_empty))
            Et_new_password_change_pass_repeat.requestFocus()
            i++
        }

        if(old_password.trim().length<6)
        {
            Et_old_password_change_pass.setError(getString(R.string.password_shorter_than_6_char))
            Et_old_password_change_pass.requestFocus()
            i++
        }
        if(new_password.trim().length<6)
        {
            Et_new_password_change_pass.setError(getString(R.string.password_shorter_than_6_char))
            Et_new_password_change_pass.requestFocus()
            i++
        }
        if(new_password_repeat.trim().length<6)
        {
            Et_new_password_change_pass_repeat.setError(getString(R.string.password_shorter_than_6_char))
            Et_new_password_change_pass_repeat.requestFocus()
            i++
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Et_email_change_pass.setError(getString(R.string.email_not_valid))
            Et_email_change_pass.requestFocus()
            i++
        }
        return i>0

    }
}