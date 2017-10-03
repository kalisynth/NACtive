package au.org.nac.nactive.NACtive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.UserEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * User Creation and Edit
 */

class EditUser : AppCompatActivity(){

    //Vars
    private var currentUser : String = CurrentUser.name
    private lateinit var data : KotlinReactiveEntityStore<Persistable>
    private lateinit var user : User
    private lateinit var nameEditText : EditText
    private lateinit var saveButton : Button
    private lateinit var userInfoText : TextView
    private var TAG = "EDITUSER"

    companion object {
        internal val EXTRA_PERSON_ID = "personId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user)

        nameEditText = user_setup_name_edittext
        saveButton = user_setup_save_btn
        userInfoText = user_setup_info

        data = (application as NACtiveApp).data

        val personId = intent.getIntExtra(EXTRA_PERSON_ID, -1)
        Log.i(TAG, "Checking User Status")
        if(personId == -1){
            user = UserEntity()
            setNewUser(user)
            Log.i(TAG, "New User")
        } else {
            data.findByKey(User::class, personId)
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{user -> setReturnUser(user)}
            Log.i(TAG, "Return User")
        }

        saveButton.setOnClickListener {
            saveNewUser()
        }
    }

    private fun setNewUser(user: User){
        this.user = user
        nameEditText.setHint(R.string.new_user_name)
        userInfoText.text = userInfoBuilder(user)
    }

    private fun setReturnUser(user : User){
        this.user = user
        nameEditText.setText(user.name)
        currentUser = user.name
        userInfoText.text = userInfoBuilder(user)
    }

    private fun userInfoBuilder(user: User) : String{
        this.user = user
        val userInfoComplete : String
        val userInfoStart = getString(R.string.user_info_start)
        var userCreationDate = getString(R.string.user_info_creation_date) + user.createdDate
        val userCurrentSession = getString(R.string.user_info_current_session) + user.currentSession
        val userPreviousSession = getString(R.string.user_info_previous_session) + user.previousSession
        val userNextSession = getString(R.string.user_info_next_session) + user.nextSession

        if(user.createdDate == null){
            userCreationDate = getString(R.string.user_info_creation_date) + " Not Available"
        }

        userInfoComplete = userInfoStart + "\n" +
                userCreationDate + "\n" +
                userCurrentSession + "\n" +
                userPreviousSession + "\n" +
                userNextSession

        Log.i(TAG, "User Info: " + userInfoComplete)
        return userInfoComplete
    }

    private fun saveNewUser(){
        user.name = nameEditText.text.toString()
        user.createdDate = NACiveUtils.getDate(System.currentTimeMillis())
        val observable = if(user.id == 0) data.insert(user) else data.update(user)
                observable.subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{_ -> finish()}
        currentUser = nameEditText.text.toString()
    }
}