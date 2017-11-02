package au.org.nac.nactive.model

import java.util.Date

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Exercise Session Model
 */

@Entity
data class WorkOutSession (
    @Id var id: Long = 0,
    var name: String? = "",
    var frequencySchedule: String? = "",
    var minTime: Long = 0,
    var overallTotal: Int = 0,
    var areaOfFocus : String? = "",
    var nextDate: Date? = null
){
    @Backlink
    lateinit var exercises : List<Exercise>
}