package com.example.printers_3d

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.Btn_show_pass
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {
    val TAG="Registration TAG"
    private val mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
    }

    fun RegistrationButton(view: View)
    {
        val name:String=Et_name_registration.text?.trim().toString()
        val email:String=Et_email_registration.text?.trim().toString()
        val password:String=Et_password_registration.text?.trim().toString()
        val password_repeat:String=Et_password_registration_repeat.text?.trim().toString()
        val check_fields:Boolean =CheckFields(name, email, password, password_repeat)
        val description:String=""
        if(check_fields)
        {
           return
        }

        if(password.equals(password_repeat))
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       Progress_bar_registration.visibility= View.VISIBLE
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")

                FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().uid.toString()).
                    setValue(User(name, email,description)).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful)
                        {
                            Toast.makeText(this,getString(R.string.registration_sucessful),Toast.LENGTH_LONG).show();
                            Et_name_registration.text.clear()
                            Et_email_registration.text.clear()
                            Et_password_registration.text.clear()
                            Et_password_registration_repeat.text.clear()
                            if(mAuth.currentUser!=null)
                            {
                                val intent=Intent(this,MainActivity::class.java)
                                startActivity(intent)
                                this.finish()
                            }
                            Progress_bar_registration.visibility= View.GONE
                        } else {
                            Toast.makeText(this,getString(R.string.registration_error),Toast.LENGTH_LONG).show();
                            Progress_bar_registration.visibility= View.GONE
                        }


                    }
                       // val user = auth.currentUser

                    } else {
                        Progress_bar_registration.visibility= View.GONE
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, getString(R.string.hint_register_error), Toast.LENGTH_SHORT).show()
                     //   updateUI(null)
                    }

                    // ...
                }

        }
        else
        {
            Et_password_registration.setError(getString(R.string.passwords_not_same))
            Et_password_registration_repeat.setError(getString(R.string.passwords_not_same))
            return
        }

    }
    fun ShowPassword(view: View)
    {
        if(view.getId()==R.id.Btn_show_pass) {
            if(Et_password_registration.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance())){
                Btn_show_pass.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_password_registration.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                Btn_show_pass.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_password_registration.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        if(view.getId()==R.id.Btn_show_pass_repeat) {
            if(Et_password_registration_repeat.getTransformationMethod().equals(
                    PasswordTransformationMethod.getInstance()
                )){
                Btn_show_pass_repeat.setImageResource(R.drawable.eye_not_open)
                //Show Password
                Et_password_registration_repeat.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance()
                );
            }
            else{
                Btn_show_pass_repeat.setImageResource(R.drawable.eye_open)
                //Hide Password
                Et_password_registration_repeat.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    fun CheckFields(name: String, email: String, password: String, password_repeat: String):Boolean
    {
        var i:Int=0;
        if(name.trim().isEmpty())
        {
            Et_name_registration.setError(getString(R.string.field_cant_be_empty))
            Et_name_registration.requestFocus()
            i++
        }
        if(email.trim().isEmpty())
        {
            Et_email_registration.setError(getString(R.string.field_cant_be_empty))
            Et_email_registration.requestFocus()
            i++


        }
        if(password.trim().isEmpty())
        {
            Et_password_registration.setError(getString(R.string.field_cant_be_empty))
            Et_password_registration.requestFocus()
            i++
        }
        if(password_repeat.trim().isEmpty())
        {
            Et_password_registration_repeat.setError(getString(R.string.field_cant_be_empty))
            Et_password_registration_repeat.requestFocus()
            i++
        }
        if(name.trim().length<3)
        {
            Et_name_registration.setError(getString(R.string.name_shorter_than_3_char))
            Et_name_registration.requestFocus()
            i++
        }

        if(password.trim().length<6)
        {
            Et_password_registration.setError(getString(R.string.password_shorter_than_6_char))
            Et_password_registration.requestFocus()
            i++
        }
        if(password_repeat.trim().length<6)
        {
            Et_password_registration_repeat.setError(getString(R.string.password_shorter_than_6_char))
            Et_password_registration_repeat.requestFocus()
            i++
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Et_email_registration.setError(getString(R.string.email_not_valid))
            Et_email_registration.requestFocus()
            i++
        }
        return i>0

    }
}