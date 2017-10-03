package au.org.nac.nactive.Utils

import au.org.nac.nactive.BuildConfig
import se.simbio.encryption.Encryption
import java.util.*

/**
 * Password Stuff
 */

object PasswordUtil {
    private val random = Random()
    private val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val KEY = BuildConfig.EN_KEY
    private val bytes = ByteArray(32)

    fun encryptPassword(password: String) : String{
        val newSalt = getSalt()
        val encryption = Encryption.getDefault(KEY, newSalt, bytes)
        val encrypted = encryption.encryptOrNull(password)

        return encrypted + ":" + newSalt
    }

    fun checkPassword(newpassword: String, salt: String, oldpassword: String){
        val encryption = Encryption.getDefault(KEY, salt, bytes)
        val decrypted = encryption.decryptOrNull(oldpassword)
        if(newpassword == decrypted){
            //Todo Success
        } else {
            //Todo Failure
        }
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
}