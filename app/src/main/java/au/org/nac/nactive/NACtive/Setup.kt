package au.org.nac.nactive.NACtive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.Utils.LoginUtils
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.User_
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_setup.*

class Setup : AppCompatActivity() {

    private var currentUser = CurrentUser.name
    private var currentUserId = CurrentUser.userId
    private var user : User? = User()
    private var TAG = "SETUP"
    private var userId : Long = 0

    private lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        userBox = (application as NACtiveApp).boxStore.boxFor(User::class.java)

        if(currentUserId != userId){
            try{
                userQuery = userBox.query().equal(User_.id, currentUserId).build()
                user = userQuery.findUnique()
                Log.i(TAG, "User Set to: " + user?.id)
            } catch(e: NoSuchElementException) {
                Log.i(TAG, "User Collection is Empty")
                userId = 0
            }
        }

        /*setup_user_btn.setOnClickListener {
            val intent = Intent(this, EditUser::class.java)
            intent.putExtra("EXTRA_PERSON_ID", userId)
            startActivity(intent)
        }*/

        setup_session_btn.setOnClickListener {
            val intent = Intent(this, EditWorkOut::class.java)
            startActivity(intent)
        }

        setup_exercise_btn.setOnClickListener {
            val intent = Intent(this, EditExercise::class.java)
            startActivity(intent)
        }

        setup_home_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
