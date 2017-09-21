package au.org.nac.nactive.Models

/*
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
*/

/**
 * Exericse Session Model
 */

//@Entity
        class ExerciseSession {
    //@Id
    var id: Long = 0
    val name: String = ""
    val frequencySchedule: ScheduleFrequency = ScheduleFrequency.ALWAYS
    val maxTime: Long = 0
    val minTime: Long = 0
    val avgTime: Long = 0
    val overallTotal: Int = 0

    //lateinit var user: ToOne<User>

}
