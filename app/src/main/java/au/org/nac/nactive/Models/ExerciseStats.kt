package au.org.nac.nactive.Models

/*import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne*/

/**
 * Exericse Stats
 */
//@Entity
class ExerciseStats {
    //@Id
    var id: Long = 0

    val time: Long = 0
    val repetitions: Int = 0
    val totalDone: Int = 0

    //lateinit var exercise: ToOne<Exercise>
}