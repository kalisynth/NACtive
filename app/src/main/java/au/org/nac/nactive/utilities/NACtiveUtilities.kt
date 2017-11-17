package au.org.nac.nactive.utilities

import au.org.nac.nactive.model.*
import com.mcxiaoke.koi.ext.asDateString
import com.mcxiaoke.koi.ext.asString
import com.mcxiaoke.koi.ext.dateParse
import io.objectbox.Box

/**
 * NACtive Utility Class
 */
object NACtiveUtilities {

    private var dateFormat = "dd/MM"
    private var timeFormat = "HH:mm"

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

    fun returnWorkOutName(id: Long?, box : Box<WorkOutSession>) : String?{
        val checkId : Long = id as Long
        val workOutQuery = box.query().equal(WorkOutSession_.id, checkId).build()
        return workOutQuery.findUnique()?.name
    }

    fun returnBodyPart(bp : String) : BodyParts{
        when(bp){
            "Left Arm" -> return BodyParts.LEFTARM
            "Right Arm" -> return BodyParts.RIGHTARM
            "Left Hand" -> return BodyParts.LEFTHAND
            "Left Foot" -> return BodyParts.LEFTFOOT
            "Left Leg" -> return BodyParts.LEFTLEG
            "Right Hand" -> return BodyParts.RIGHTHAND
            "Right Foot" -> return BodyParts.RIGHTFOOT
            "Right Leg" -> return BodyParts.RIGHTLEG
            "Torso" -> return BodyParts.TORSO
            "Head" -> return BodyParts.HEAD
            "Neck" -> return BodyParts.NECK
            "Spine" -> return BodyParts.SPINE
            else -> return BodyParts.DEFAULT
        }
    }

    fun stringFromList(strings: List<String>) : String{
        return strings.joinToString(",")
    }

    fun returnStepsList(steps: String) : List<String>{
        return steps.split(",").map {it.trim()}
    }

    fun returnFreqEnum(freq : String) : ScheduleFrequency{
        when(freq){
            "Always" -> return ScheduleFrequency.ALWAYS
            "Scheduled" -> return ScheduleFrequency.SCHEDULED
            "Random" -> return ScheduleFrequency.RANDOM
            else -> return ScheduleFrequency.UNKNOWN
        }
    }

    fun setUserCurrentWorkOutSession(user: User, userBox: Box<User>, workOutId: Long){
        user.previousWorkOutId = user.currentWorkOutId
        user.currentWorkOutId = workOutId
        userBox.put(user)
    }

    fun setUserNextWorkOutSession(user: User, userBox: Box<User>, workOutId : Long){
        user.nextWorkOutId = workOutId
        userBox.put(user)
    }
}