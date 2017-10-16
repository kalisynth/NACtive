package au.org.nac.nactive.model

import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Exericse Session Model
 */

@Entity
data class WorkOutSession (
    @Id var id: Long = 0,
    val name: String = "",
    val frequencySchedule: String = "",
    val maxTime: Long = 0,
    val minTime: Long = 0,
    val avgTime: Long = 0,
    val overallTotal: Int = 0
)