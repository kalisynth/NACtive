package au.org.nac.nactive.Models

/*import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne*/

/**
 * Created by Cade2 on 19/09/2017.
 */

//@Entity
        class SessionStats {
    //@Id
    var id: Long = 0

    var time: Long = 0
    var completed: Boolean = false
    var exerciseStoppedOn : String = ""

    //lateinit var session: ToOne<ExerciseSession>
}