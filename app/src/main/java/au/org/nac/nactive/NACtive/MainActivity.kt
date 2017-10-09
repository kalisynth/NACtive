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
import au.org.nac.nactive.Utils.NACiveUtils
import au.org.nac.nactive.Utils.NACiveUtils.clearUser
import au.org.nac.nactive.Utils.NACiveUtils.setCurrentUser
import au.org.nac.nactive.Utils.PasswordUtil
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.UserEntity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private var TAG = "MainAct"
    private lateinit var data : KotlinReactiveEntityStore<Persistable>
    private lateinit var user : User
    private lateinit var googleApiClient : GoogleApiClient
    private lateinit var googleSignInOptions : GoogleSignInOptions
    private lateinit var gSignInBtn : SignInButton
    private lateinit var ngSignInBtn : Button
    private lateinit var signOutBtn : Button
    private lateinit var revokeBtn : Button

    private var RC_SIGN_IN = 9001
    private var acct : GoogleSignInAccount? = null

    companion object {
        internal val EXTRA_USER_ID = "userId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        data = (application as NACtiveApp).data

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        initGoogleApi()

        gSignInBtn = main_google_signin_btn
        ngSignInBtn = main_login_btn
        signOutBtn = main_signout_btn
        revokeBtn = revoke_access_btn

        main_setup_btn.setOnClickListener {
             val intent = Intent(this, Setup::class.java)
            startActivity(intent)
        }

        main_exercise_btn.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            startActivity(intent)
        }

        main_information_btn.setOnClickListener{
            val intent = Intent(this, Information::class.java)
            startActivity(intent)
        }

        gSignInBtn.setOnClickListener {
            googleSignIn()
        }

        ngSignInBtn.setOnClickListener {
            signIn()
        }

        signOutBtn.setOnClickListener {
            signOut(CurrentUser.isGoogleUser)
        }

        revokeBtn.setOnClickListener {
            revokeGoogleAccess()
        }
    }

    private fun googleSignIn(){
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    private fun signIn(){
        alert {
            title = "Sign In"
            var editUserName : EditText? = null
            var editPassword : EditText? = null

            customView {
                verticalLayout {
                    layoutParams = ViewGroup.LayoutParams(wrapContent, wrapContent)
                    linearLayout {
                        textView().text = "Name: "
                        editUserName = editText {
                            hint = "John Smith"
                        }
                        textView().text = "Password: "
                        editPassword = editText{
                            hint = "Password123"
                        }
                    }
                    linearLayout{
                        button {
                            text = "Sign In"
                            signInUser("${editUserName!!.text}", "${editPassword!!.text}", false)
                            Log.d(TAG, "Sign In: " + "${editUserName!!.text}" + "${editPassword!!.text}")
                        }
                        button {
                            text = "New User"
                            newUser()
                        }
                    }
                }
            }
        }.show()
    }

    private fun signInUser(name : String?, password : String?, isGoogleUser: Boolean){
        var uName : String
        var uId = 0
        var isNewUser = false
        try{
            user = data.select(User::class).where(UserEntity::name.eq(name)).get().single()
            uName = user.name
            uId = user.id
        } catch (e: RuntimeException){
            Log.e(TAG, "Exception: " + e)
            uName = acct!!.displayName.toString()
            isNewUser = true
        }
        if(!isGoogleUser){
            if(PasswordUtil.checkPassword(password, user.sodium, user.password)){
                //Success Login
                Toast.makeText(this, "Welcome Back " + user.name, Toast.LENGTH_SHORT).show()
                setCurrentUser(uName, uId, false)
            } else {
                //Failure Login
                Log.e(TAG, "Non Google Login Attempt Failed")
                signInFail()
            }
        } else {
            setCurrentUser(uName, uId, true)
            Toast.makeText(this, "Successfully Signed in as: " + acct!!.displayName, Toast.LENGTH_LONG).show()
            if(acct!!.id!=null){
                val gId = acct!!.id.toString()
                CurrentUser.googleUUID = gId
                if(isNewUser){
                    Log.i(TAG, "Inserting New User: " + acct!!.givenName.toString())
                    user = UserEntity()
                    user.name = acct!!.givenName.toString()
                    user.createdDate = NACiveUtils.getDate(System.currentTimeMillis())
                    user.isGoogleUser = true
                    user.googleUUID = acct!!.id.toString()
                    user.password = acct!!.displayName.toString()
                    data.insert(user)
                    Log.i(TAG, "User Id: " + user.id)
                }
            }
        }
        updateUi(true)
    }

    private fun newUser(){
        val intent = Intent(this, EditUser::class.java)
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
            acct = result.signInAccount
            signInUser(acct!!.givenName, acct!!.displayName, true)
        } else {
            Log.e(TAG, "Google Login attempt Failed Intent: " + data1 + "resultCode: " + resultCode)
            signInFail()
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
            user = UserEntity()
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
