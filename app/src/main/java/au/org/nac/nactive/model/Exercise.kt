package au.org.nac.nactive.model
import io.objectbox.annotation.*
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Exercise Model
 */

@Entity
data class Exercise(
    @Id var id: Long = 0,
    var name: String? = "",
    var description: String? = "",
    var support : Boolean = false,
    var minTime: Long = 0,
    var difficultylevel: Int = 0,
    var experience: Int = 0,
    var overallTotal: Int = 0,
    var stepsStrings: String? = "",
    var videoLocation: String? = ""
)/*{
    @Backlink
    lateinit var workout : ToOne<Exercise>
}*/
