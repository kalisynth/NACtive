package au.org.nac.nactive.Utils

import android.app.FragmentManager
import android.app.FragmentTransaction
import com.mcxiaoke.koi.ext.asDateString
import com.mcxiaoke.koi.ext.asString
import com.mcxiaoke.koi.ext.dateParse

/**
 * NACtive Utility Class
 */
object NACiveUtils {

    var dateFormat = "dd/MM"
    var timeFormat = "HH:mm"

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit){
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun getDate(ttf: Long) : String{
        val dString = ttf.asDateString()
        return dateParse(dString).asString(dateFormat)
    }

    fun getTime(ttf: Long) : String{
        val dString = ttf.asDateString()
        val date = dateParse(dString)
        return date.asString(timeFormat)
    }


}