package au.org.nac.nactive.Models

import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.ManyToOne


/**
 * Session Stats Interface Model
 */

@Entity
interface SessionStats {
    @get:Key
    @get:Generated
    var id: Int

    var time: Long
    var completed: Boolean
    var exerciseStoppedOn : String

    @get:ManyToOne
    var exerciseSession : ExerciseSession
}