package au.org.nac.nactive.Models

/*import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id*/

/**
 * User Model
 */

//@Entity
class User {
    //@Id
    var id: Long = 0
    val name: String = ""
    val nextSession: ExerciseSession = ExerciseSession()
    val currentSession: ExerciseSession = ExerciseSession()
    val previousSession: ExerciseSession = ExerciseSession()

    //@Backlink
    lateinit var sessions: List<ExerciseSession>

}