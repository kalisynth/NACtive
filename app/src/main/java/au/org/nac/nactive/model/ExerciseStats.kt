package au.org.nac.nactive.model
import io.objectbox.annotation.*
import io.objectbox.relation.ToOne

/**
 * Exericse Stats
 */

@Entity
data class ExerciseStats (
    @Id var id: Long = 0,
    val time: Long = 0,
    val repetitions: Int = 0,
    val totalDone: Int = 0,
    val exerciseName: String = ""
)