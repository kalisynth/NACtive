package au.org.nac.nactive.model
import io.objectbox.annotation.*
import io.objectbox.relation.ToMany

/**
 * Exercise Model
 */

@Entity
data class Exercise(
    @Id var id: Long = 0,
    val name: String = "",
    val description: String = "",
    val repetitions: Int = 0,
    val maxTime: Long = 0,
    val minTime: Long = 0,
    val avgTime: Long = 0,
    val difficultylevel: Int = 0,
    val experience: Int = 0,
    val overallTotal: Int = 0
)
