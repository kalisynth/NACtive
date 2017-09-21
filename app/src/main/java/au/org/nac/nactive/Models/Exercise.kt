package au.org.nac.nactive.Models

/*import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany*/

/**
 * Exercise Model
 */

//@Entity
class Exercise{
    //@Id var id: Long = 0

    val name: String = ""
    val description: String = ""
    val repetitions: Int = 0
    val maxTime: Long = 0
    val minTime: Long = 0
    val avgTime: Long = 0
    val difficultylevel: Int = 0
    val experience: Int = 0
    val overallTotal: Int = 0


    /*lateinit var exercisesession: ToMany<ExerciseSession>

    @Backlink
    lateinit var exerciseStatsList: List<ExerciseStats>*/
}