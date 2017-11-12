package au.org.nac.nactive.NACtive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import au.org.nac.nactive.R
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.model.Constants.Companion.USERIDKEY
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.User_
import au.org.nac.nactive.model.WorkOutSession
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.fragment_user.*
import javax.inject.Inject

/**
 * User Creation and Edit
 */

class EditUser : AppCompatActivity(){

    //Vars
    private var currentUser : String? = CurrentUser.name
    private var user : User? = User()
    private lateinit var nameEditText : EditText
    private lateinit var saveButton : Button
    private lateinit var userInfoText : TextView
    private var TAG = "USER_EDITOR"

    //Dagger Injection
    @Inject
    lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>

    @Inject
    lateinit var workOutBox : Box<WorkOutSession>

    companion object {
        internal val EXTRA_USER_ID = USERIDKEY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user)

        //INIT UI

        nameEditText = user_setup_name_edittext
        saveButton = user_setup_save_btn
        userInfoText = user_setup_info

        //Get User ID
        val userId = intent.getLongExtra(EXTRA_USER_ID, -1)
        Log.i(TAG, "Checking User Status")

        //Check if New User
        if(userId < 1){
            setNewUser(user)
            Log.i(TAG, "New User")
        } else {
            userQuery = userBox.query().equal(User_.id, userId).build()
            val returnUser = userQuery.findUnique() as User
           setReturnUser(returnUser)
        }

        //Set OnClickListener
        saveButton.setOnClickListener {
            saveUser()
        }
    }

    private fun setNewUser(user: User?){
        //Set New User
        this.user = user
        nameEditText.setText(CurrentUser.name)
            nameEditText.setHint(R.string.new_user_name)
            if(userInfoText.visibility == View.VISIBLE){
                userInfoText.visibility = View.GONE
            }
        }

    private fun setReturnUser(user : User){
        //Return User
        this.user = user
        try{
            nameEditText.setText(user.name)
            currentUser = user.name
            userInfoText.text = userInfoBuilder(user)

            if(userInfoText.visibility == View.GONE){
                userInfoText.visibility = View.VISIBLE
            }
        } catch(e: NullPointerException){
            Log.e(TAG, "Setting Return User Error: " + e)
        }
    }

    private fun userInfoBuilder(user: User) : String{
        //Build Information Display
        this.user = user
        val userInfoComplete : String
        val userInfoStart = getString(R.string.user_info_start)
        val userCreationDate = getString(R.string.user_info_creation_date) + user.createdDate

        val userCurrentWO = getString(R.string.user_info_current_session) +
                NACiveUtils.returnWorkOutName(user.currentWorkOutId, workOutBox) //Get Current WorkOut
        val userPreviousWO = getString(R.string.user_info_previous_session) +
                NACiveUtils.returnWorkOutName(user.previousWorkOutId, workOutBox) //Get Previous WorkOut
        val userNextWO = getString(R.string.user_info_next_session) +
                NACiveUtils.returnWorkOutName(user.nextWorkOutId, workOutBox) //Get Next WorkOut

        userInfoComplete = userInfoStart + "\n" +
                userCreationDate + "\n" +
                userCurrentWO + "\n" +
                userPreviousWO + "\n" +
                userNextWO

        Log.i(TAG, "User Info: " + userInfoComplete)
        return userInfoComplete
    }

    private fun saveUser(){
        currentUser = nameEditText.text.toString()
    }
}