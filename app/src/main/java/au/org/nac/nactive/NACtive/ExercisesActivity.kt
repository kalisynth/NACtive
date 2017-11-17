package au.org.nac.nactive.nactive

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.R
import au.org.nac.nactive.model.Constants.Companion.USERIDKEY
import au.org.nac.nactive.model.Constants.Companion.WORKOUTIDKEY

class ExercisesActivity : AppCompatActivity() {

    lateinit var cUser : String
    var loggedInUser = false

    val TAG = "EXERCISEACT"

    companion object {
        internal val EXTRA_WORKOUT_ID = WORKOUTIDKEY
        internal val EXTRA_USER_ID = USERIDKEY
    }

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
