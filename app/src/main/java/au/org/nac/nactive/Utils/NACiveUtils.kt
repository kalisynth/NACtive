package au.org.nac.nactive.Utils

import android.app.FragmentManager
import android.app.FragmentTransaction
import au.org.nac.nactive.model.CurrentUser
import au.org.nac.nactive.model.WorkOutSession
import au.org.nac.nactive.model.WorkOutSession_
import com.mcxiaoke.koi.ext.asDateString
import com.mcxiaoke.koi.ext.asString
import com.mcxiaoke.koi.ext.dateParse
import io.objectbox.Box

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

    fun clearUser(){
        CurrentUser.name = ""
        CurrentUser.userId = 0
        CurrentUser.isGoogleUser = false
    }

    fun setCurrentUser(name : String?, userId: Long?, isGUser : Boolean?){
        CurrentUser.name = name!!
        CurrentUser.userId = userId!!
        CurrentUser.isGoogleUser = isGUser!!
    }

    fun returnWorkOutName(id: Long, box : Box<WorkOutSession>) : String?{
        val workOutQuery = box.query().equal(WorkOutSession_.id, id).build()
        return workOutQuery.findUnique()?.name
    }
}