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
import kotlinx.android.synthetic.main.activity_change_email.*
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_change_password.Btn_show_pass_old
import kotlinx.android.synthetic.main.activity_change_password.Et_email_change_pass
import kotlinx.android.synthetic.main.activity_change_password.Progress_bar_change_pass
import kotlinx.android.synthetic.main.activity_user_settings.*
import java.util.*

class ChangeEmailActivity : AppCompatActivity() {
    val TAG="ChangeEmail TAG"
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        if(Locale.getDefault().getLanguage()=="pl")
        {
            mAuth.setLanguageCode("pl")
        }
        else
        {
            mAuth.setLanguageCode("en")
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
    }
    fun ChangeEmailButton(view: View) {
        val email: String = Et_email_change_email.text?.trim().toString()
        val old_password: String = Et_password_change_email.text?.trim().toString()
        val new_email: String = Et_new_email_enter.text?.trim().toString()
        val new_email_repeat: String = Et_new_email_repeat.text?.trim().toString()
        val check_fields: Boolean = CheckFields(email, old_password, new_email, new_email_repeat)
        if (check_fields) {
            return
        }
        if (new_email.equals(new_email_repeat)) {
            Progress_bar_change_email.visibility = View.VISIBLE
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
                        Toast.makeText(
                            this,
                            getString(R.string.auth_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(TAG, "User re-authenticated.")
                        Toast.makeText(this, getString(R.string.auth_error), Toast.LENGTH_SHORT)
                            .show()
                        return@addOnCompleteListener

                    }

                }
            user.updateEmail(new_email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                        Toast.makeText(
                            this,
                            getString(R.string.email_changed_success),
                            Toast.LENGTH_LONG
                        ).show()
                        Et_email_change_email.getText().clear()
                        Et_password_change_email.getText().clear()
                        Et_new_email_enter.getText().clear()
                        Et_new_email_repeat.getText().clear()

                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().uid.toString()).child("email")
                            .setValue(new_email)
                            .addOnCompleteListener { task2 ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "Email in profile updated successfully.")
                                    Toast.makeText(
                                        this,
                                        getString(R.string.email_profile_changed_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Log.d(TAG, "User password updated error.")
                                    Toast.makeText(
                                        this,
                                        getString(R.string.password_changed_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        Et_new_email_enter.setError(getString(R.string.passwords_not_same))
                        Et_new_email_repeat.setError(getString(R.string.passwords_not_same))
                    }
                    Progress_bar_change_email.visibility = View.GONE
                }
        }
    }
            fun ShowPassword(view: View) {
                if (view.getId() == R.id.Btn_show_pass_old_in_email) {
                    if (Et_password_change_email.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        Btn_show_pass_old_in_email.setImageResource(R.drawable.eye_not_open)
                        //Show Password
                        Et_password_change_email.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        Btn_show_pass_old_in_email.setImageResource(R.drawable.eye_open)
                        //Hide Password
                        Et_password_change_email.setTransformationMethod(
                            PasswordTransformationMethod.getInstance()
                        );
                    }
                }
            }

            fun CheckFields(
                email: String,
                old_password: String,
                new_email: String,
                new_email_repeat: String
            ): Boolean {
                var i: Int = 0;

                if (email.trim().isEmpty()) {
                    Et_email_change_email.setError(getString(R.string.field_cant_be_empty))
                    Et_email_change_email.requestFocus()
                    i++
                }
                if (old_password.trim().isEmpty()) {
                    Et_old_password_change_pass.setError(getString(R.string.field_cant_be_empty))
                    Et_old_password_change_pass.requestFocus()
                    i++
                }
                if (new_email.trim().isEmpty()) {
                    Et_new_email_enter.setError(getString(R.string.field_cant_be_empty))
                    Et_new_email_enter.requestFocus()
                    i++
                }
                if (new_email_repeat.trim().isEmpty()) {
                    Et_new_email_repeat.setError(getString(R.string.field_cant_be_empty))
                    Et_new_email_repeat.requestFocus()
                    i++
                }

                if (old_password.trim().length < 6) {
                    Et_old_password_change_pass.setError(getString(R.string.password_shorter_than_6_char))
                    Et_old_password_change_pass.requestFocus()
                    i++
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Et_email_change_email.setError(getString(R.string.email_not_valid))
                    Et_email_change_email.requestFocus()
                    i++
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(new_email).matches()) {
                    Et_new_email_enter.setError(getString(R.string.email_not_valid))
                    Et_new_email_enter.requestFocus()
                    i++
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(new_email_repeat).matches()) {
                    Et_new_email_repeat.setError(getString(R.string.email_not_valid))
                    Et_new_email_repeat.requestFocus()
                    i++
                }
                return i > 0


        }
    }
