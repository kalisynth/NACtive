package au.org.nac.nactive.Utils

import android.util.Log
import au.org.nac.nactive.BuildConfig
import au.org.nac.nactive.NACtiveApp
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.User
import au.org.nac.nactive.model.User_
import io.objectbox.Box
import se.simbio.encryption.Encryption
import java.util.*

/**
 * Password Stuff
 */

object LoginUtils {
    private val random = Random()
    private val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val KEY = BuildConfig.EN_KEY
    private val bytes = ByteArray(32)
    private lateinit var userBox : Box<User>
    lateinit var user : User
    private val TAG  = "LoginUtils"

    fun encryptPassword(password: String) : String{
        val newSalt = getSalt()
        val encryption = Encryption.getDefault(KEY, newSalt, bytes)
        val encrypted = encryption.encryptOrNull(password)

        return encrypted + ":" + newSalt
    }

    fun checkPassword(newPassword: String?, salt: String, oldPassword: String) : Boolean{
        val encryption = Encryption.getDefault(KEY, salt, bytes)
        val decrypted = encryption.decryptOrNull(oldPassword)
        /*if(newPassword == decrypted){
            return true
        } else {
            return false
        }*/
        return(newPassword == decrypted)
    }

    fun resetPassword(){

    }

    fun getSalt() : String{
        val length = rand(20, 40)
        val returnVal = StringBuilder(length)

        var i = 0
        while(i < length){
            returnVal.append(ALPHABET[rand(0, ALPHABET.length)])
        }

        return String(returnVal)
    }

    fun rand(from: Int, to: Int) : Int{
        return random.nextInt(to - from) + from
    }

    fun googleUserSignIn(id: Long, name : String, googleUID: String, box: Box<User>){
        val newId : Long = 0
        if(id == newId){
            user = User()
            user.name = name
            user.googleUID = googleUID
            user.createdDate = NACiveUtils.getDate(System.currentTimeMillis())
            box.put(user)
            Log.i(TAG, "New User Signed In" + user.id)
        } else {
            NACiveUtils.setCurrentUser(name, id, true)
            Log.i(TAG, "User Signed in " + id)
        }
    }

    fun userSignOut(){
        CurrentUser.name = ""
        CurrentUser.userId = 0
        CurrentUser.isGoogleUser = false
    }

    fun userSignIN(id: Long, name: String, googleUID : String, box: Box<User>){
        val newId : Long = 0
        if(id == newId){
            user = User()
            user.name = name
            user.googleUID = googleUID
            user.createdDate = NACiveUtils.getDate(System.currentTimeMillis())
            box.put(user)
        } else {
            NACiveUtils.setCurrentUser(name, id, isGUser = false)
        }
    }

    fun isNewUser(name: String?, box: Box<User>): Boolean{
        var names : Long = 0
        val z : Long = 0
        if(name != null){
            names = box.query().equal(User_.name, name).build().count()
        }
        Log.d(TAG, "isNewUser name: " + name + " count = " + names)
        return(names == z)
    }
}