package au.org.nac.nactive.NACtive

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.R
import au.org.nac.nactive.Utils.NACiveUtils.clearUser
import au.org.nac.nactive.Utils.LoginUtils
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.User_
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import io.objectbox.Box
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private var TAG = "MainAct"
    private lateinit var userName : String
    private lateinit var userGoogleIdToken : String
    private lateinit var googleApiClient : GoogleApiClient
    private lateinit var googleSignInOptions : GoogleSignInOptions

    //Buttons
    private lateinit var gSignInBtn : SignInButton
    private lateinit var ngSignInBtn : Button
    private lateinit var signOutBtn : Button
    private lateinit var revokeBtn : Button

    //ObjectBox
    private lateinit var userBox : Box<User>
    private lateinit var userQuery : Query<User>

    private var RC_SIGN_IN = 9001
    private var acct : GoogleSignInAccount? = null //Google Account

    companion object {
        internal val EXTRA_USER_ID = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set ObjectBox
        userBox = (application as NACtiveApp).boxStore.boxFor(User::class.java)

        //Google Sign in Builder
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        //Build Google Api
        initGoogleApi()

        //Assign Buttons from Layout
        gSignInBtn = main_google_signin_btn
        ngSignInBtn = main_login_btn
        signOutBtn = main_signout_btn
        revokeBtn = revoke_access_btn

        //Setup Button
        main_setup_btn.setOnClickListener {val intent = Intent(this,
                Setup::class.java)
            startActivity(intent)
        }

        //Exercise Button
        main_exercise_btn.setOnClickListener {val intent = Intent(this,
                ExercisesActivity::class.java)
            startActivity(intent)
        }

        //Information Button
        main_information_btn.setOnClickListener{val intent = Intent(this,
                Information::class.java)
            startActivity(intent)
        }

        //User Diary Button
        main_user_diary_btn.setOnClickListener { val intent = Intent(this,
                UserDiary::class.java)
        startActivity(intent)}

        //Sign In Button
        gSignInBtn.setOnClickListener {
            googleSignIn()
        }

        //Sign Out Button
        signOutBtn.setOnClickListener {
            LoginUtils.userSignOut()
            updateUi(false)
            Log.i(TAG, "User Signed Out")
        }

        //Revoke Button
        revokeBtn.setOnClickListener {
            revokeGoogleAccess()
        }
    }

    private fun googleSignIn(){
        //Sign in to Google
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun newUser(){
        val intent = Intent(this, EditWorkOut::class.java)
        val user = User()
        user.name = CurrentUser.name
        user.createdDate = NACiveUtils.getDate(System.currentTimeMillis())
        user.googleUID = CurrentUser.googleUUID
        user.userLevel = 1
        user.userExperience = 0
        userBox.put(user)
        Log.d(TAG, "User Object Id: " + user.id)
        alert("Looks like this is your first time here in NACtive, would you like to set up" +
                " your first work out?", "Hello " + user.name) {
            yesButton {
                startActivity(intent)
            }
            noButton { //Do Nothing
            }
        }.show()
        updateUi(true)
    }

    private fun returnUser(user : User){
        val intent = Intent(this, ExercisesActivity::class.java)
        val exp2lvl = R.integer.experience2Level - user.userExperience
        val nextLevel = user.userLevel + 1
        alert("Welcome Back " + CurrentUser.name + " You are currently Level: "
                + user.userLevel + " you have " + user.userExperience + " experience and " + exp2lvl
                + "experience to go till level " + nextLevel + " would you like to start your Work Out?",
                "Signed in successfully"){
            yesButton {
                startActivity(intent) //Start Exercises
            }
            noButton {
                //do nothing
            }
        }.show()
        updateUi(true)
    }

    private fun initGoogleApi(){
        googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(Auth.CREDENTIALS_API)
                .build()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data2: Intent?) {
        super.onActivityResult(requestCode, resultCode, data2)

        when(requestCode){
            RC_SIGN_IN -> handleGoogleSignInResult(resultCode, data2)
        }
    }

    private fun handleGoogleSignInResult(resultCode: Int, data1: Intent?){
        Log.d(TAG, "handleSignInResult: " + resultCode +
                Auth.GoogleSignInApi.getSignInResultFromIntent(data1))
        //Get Google Sign In Result and Handle it
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data1)
        if(result.isSuccess){
            acct = result.signInAccount //Get Account from Result
            userName = acct!!.displayName.toString() //Set UserName
            userGoogleIdToken = acct!!.idToken.toString() //Set GoogleId Token

            userQuery = userBox.query().equal(User_.name, userName).build()

            val signingInUser = userQuery.findUnique() //Get User
            val userNameCheck : String?

            Log.i(TAG, "User Query Result: " + signingInUser?.name + " acct name: " + userName)

            if(signingInUser?.name == null){
                userNameCheck = acct?.displayName.toString()
                Log.d(TAG, "UserName: acct, " + userNameCheck)
            } else {
                userNameCheck = signingInUser.name
                Log.d(TAG, "UserName: signInUser, " + userNameCheck)
            }

            Log.d(TAG, "Is New User? " + LoginUtils.isNewUser(userNameCheck, userBox))

            if(LoginUtils.isNewUser(userNameCheck, userBox)){
                Log.i(TAG, "New Account Name: " + userNameCheck)
                CurrentUser.name = userNameCheck.toString()
                CurrentUser.isGoogleUser = true
                CurrentUser.googleUUID = acct?.id.toString()
                Log.d(TAG, "New User GoogleId: " + acct?.id.toString())
                newUser()
            } else if(signingInUser?.name != null){
                LoginUtils.googleUserSignIn(signingInUser.id, userName, userGoogleIdToken, userBox)
                Log.d(TAG, "Return User")
                returnUser(signingInUser)
            }
        } else {
            Log.e(TAG, "Google Login attempt Failed Intent: " + data1 + "resultCode: " + resultCode)
            signInFail() //Sign in Fail
        }
    }

    private fun signInFail(){
        Toast.makeText(this, "Sign In Attempt Failed, Please try again", Toast.LENGTH_LONG).show()
        clearUser()
    }

    private fun updateUi(signedIn: Boolean){
        if(signedIn){
            //Update UI to signing out
            gSignInBtn.visibility = View.GONE
            //ngSignInBtn.visibility = View.GONE
            signOutBtn.visibility = View.VISIBLE
            revokeBtn.visibility = View.VISIBLE
            Log.i(TAG, "User Signed In")
        } else {
            //update UI to signing in
            gSignInBtn.visibility = View.VISIBLE
            //ngSignInBtn.visibility = View.VISIBLE
            signOutBtn.visibility = View.GONE
            revokeBtn.visibility = View.GONE
            Log.i(TAG, "User Signed Out")
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i(TAG, "OnConnectionFailed: " + p0)
    }

    override fun onConnected(p0: Bundle?) {
        Log.i(TAG, "Google Sign In onConnected: " + p0)
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.i(TAG, "Google Sign In onConnectionSuspended: " + p0)
    }

    private fun revokeGoogleAccess(){
        if(googleApiClient.isConnected){
            Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback {
                ResultCallback<Status> {
                    @Override
                    fun onResult(status: Status){
                        Log.i(TAG, "User revoked Google Access")
                    }
                }
            }
        }

    }
}
