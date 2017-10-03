package au.org.nac.nactive.model

import io.requery.*

/**
 * Exercise Model
 */

@Entity
interface Exercise : Persistable{
    @get:Key
    @get:Generated
    var id: Int
    val name: String
    val description: String
    val repetitions: Int
    val maxTime: Long
    val minTime: Long
    val avgTime: Long
    val difficultylevel: Int
    val experience: Int
    val overallTotal: Int

    @get:Convert(StringListConverter::class)
    val steps: ArrayList<String>

    @get:ManyToMany
    val exerciseSessions : ExerciseSession

    @get:OneToMany(mappedBy = "exercise")
    val exerciseStats : MutableSet<ExerciseStats>
}
