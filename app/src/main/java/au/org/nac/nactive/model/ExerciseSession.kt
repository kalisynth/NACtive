package au.org.nac.nactive.model

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

    @get:ManyToOne
    var user : User

    @get:ManyToMany(mappedBy = "exerciseSessions")
    @get:JunctionTable
    val getExercises : List<Exercise>

    @get:OneToMany
    val sessionStats : MutableSet<SessionStats>
}