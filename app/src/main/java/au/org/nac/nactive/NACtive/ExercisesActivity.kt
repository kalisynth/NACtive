package au.org.nac.nactive.NACtive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.R

class ExercisesActivity : AppCompatActivity() {

    lateinit var cUser : String
    var loggedInUser = false

    val TAG = "EXERCISEACT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        cUser = CurrentUser.name
    }

    fun checkCurrentUser(){
        if(cUser == ""){

        }
    }
}
