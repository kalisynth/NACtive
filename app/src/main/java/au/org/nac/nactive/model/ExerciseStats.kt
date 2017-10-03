package au.org.nac.nactive.model

import io.requery.Entity
import io.requery.Generated
import io.requery.Key
import io.requery.ManyToOne

/**
 * Exericse Stats
 */

@Entity
interface ExerciseStats {
    @get:Key
    @get:Generated
    var id: Int
    val time: Long
    val repetitions: Int
    val totalDone: Int

    @get:ManyToOne
    var exercise : Exercise
}