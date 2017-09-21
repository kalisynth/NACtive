package au.org.nac.nactive.Models

import io.requery.*

/*import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany*/

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

    @get:ManyToMany
    val exerciseSession : Set<ExerciseSession>

    @get:OneToMany(mappedBy = "exercise")
    val exerciseStats: MutableSet<ExerciseStats>
}
