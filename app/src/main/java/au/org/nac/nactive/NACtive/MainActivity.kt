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
        main_setup_btn.setOnClickListener {
             val intent = Intent(this, Setup::class.java)
            startActivity(intent)
        }

        //Exercise Button
        main_exercise_btn.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            startActivity(intent)
        }

        //Information Button
        main_information_btn.setOnClickListener{
            val intent = Intent(this, Information::class.java)
            startActivity(intent)
        }

        //Sign In Button
        gSignInBtn.setOnClickListener {
            googleSignIn()
        }

        //Sign Out Button
        signOutBtn.setOnClickListener {
            signOut(CurrentUser.isGoogleUser)
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
        //Open User Editor
        val intent = Intent(this, EditUser::class.java)
        CurrentUser.userId = 0
        startActivity(intent)
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
        Log.d(TAG, "handleSignInResult: " + resultCode + Auth.GoogleSignInApi.getSignInResultFromIntent(data1))
        //Get Google Sign In Result and Handle it
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data1)
        if(result.isSuccess){
            acct = result.signInAccount //Get Account from Result
            userName = acct!!.displayName.toString() //Set UserName
            userGoogleIdToken = acct!!.idToken.toString() //Set GoogleId Token

            try{
                userQuery = userBox.query().equal(User_.name, userName).build()
                //Return User
            } catch(e: NullPointerException){
                Log.e(TAG, "Null Pointer Exception" + e)
                //is New User
            }

            val signingInUser = userQuery.findUnique() //Get User

            if(signingInUser != null && LoginUtils.isNewUser(signingInUser.name, userBox)){
                LoginUtils.googleUserSignIn(0, userName, userGoogleIdToken, userBox)
                newUser()
            } else if(signingInUser != null){
                LoginUtils.googleUserSignIn(signingInUser.id, userName, userGoogleIdToken, userBox)
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
            ngSignInBtn.visibility = View.GONE
            signOutBtn.visibility = View.VISIBLE
            revokeBtn.visibility = View.VISIBLE
            Log.i(TAG, "User Signed In")
        } else {
            //update UI to signing in
            gSignInBtn.visibility = View.VISIBLE
            ngSignInBtn.visibility = View.VISIBLE
            signOutBtn.visibility = View.GONE
            revokeBtn.visibility = View.GONE
            Log.i(TAG, "User Signed Out")
        }
    }

    private fun signOut(isGoogleUser: Boolean){
        if(isGoogleUser == CurrentUser.isGoogleUser){
            if(googleApiClient.isConnected){
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback {
                    ResultCallback<Status>{
                        @Override
                        fun onResult(status: Status){
                            //do Something
                            Log.i(TAG, "User Signed out" + status)
                        }
                    }
                }
            }
        } else {
            //user = UserEntity()
            clearUser()
        }
        updateUi(false)
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
