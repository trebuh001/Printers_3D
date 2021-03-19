package com.example.printers_3d
import android.content.Context
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs",Context.MODE_PRIVATE)
        val sharedPrefsEdit: SharedPreferences.Editor= appSettingsPrefs.edit()
        val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode",false)

        if(isNightModeOn)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            main_light_night_button.text=getString(R.string.main_activity_night_mode_on)
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            main_light_night_button.text=getString(R.string.main_activity_light_mode_on)

        }
    }

    /*val intent = Intent(this, DisplayMessageActivity::class.java).apply {
        putExtra(EXTRA_MESSAGE, message)
    startActivity(intent)*/
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuth.currentUser!=null)
        {
            val intent=Intent(this,UserSettingsActivity::class.java)
            startActivity(intent)
            this.finish()
        }

    }
    fun Login(v: View)
    {
        val intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
    fun Registration(v: View)
    {
        val intent= Intent(this,RegistrationActivity::class.java)
        startActivity(intent)
    }
    fun LightNightMode(v:View)
   {
       val appSettingsPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs",Context.MODE_PRIVATE)
       val sharedPrefsEdit: SharedPreferences.Editor= appSettingsPrefs.edit()
       val isNightModeOn:Boolean= appSettingsPrefs.getBoolean("NightMode",false)
       if(isNightModeOn)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefsEdit.putBoolean("NightMode",false)
            sharedPrefsEdit.apply()
            main_light_night_button.text=getString(R.string.main_activity_night_mode_on)
        }
       else
       {
           AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
           sharedPrefsEdit.putBoolean("NightMode", true)
           sharedPrefsEdit.apply()
           main_light_night_button.text=getString(R.string.main_activity_light_mode_on)


       }
    }
    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }

}
