package au.org.nac.nactive.Models

import io.requery.*

/**
 * Exericse Session Model
 */

@Entity
interface ExerciseSession : Persistable {
    @get:Key
    @get:Generated
    var id: Int
    val name: String
    val frequencySchedule: ScheduleFrequency
    val maxTime: Long
    val minTime: Long
    val avgTime: Long
    val overallTotal: Int

    @get:ForeignKey
    @get:ManyToOne
    var user: User

    @get:OneToMany(mappedBy = "exerciseSession")
    val sessionStats: MutableSet<SessionStats>
}
