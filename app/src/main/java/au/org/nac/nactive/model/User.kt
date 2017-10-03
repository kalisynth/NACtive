package au.org.nac.nactive.model

import android.os.Parcelable
import io.requery.*

/**
 * User Model
 */

@Entity
interface User : Parcelable, Persistable {
    @get:Key
    @get:Generated
    var id: Int

    var name: String
    var createdDate: String
    var nextSession: String
    var currentSession: String
    var previousSession: String

    var password: String

    @get:OneToMany(mappedBy = "user")
    val exerciseSessions : MutableSet<ExerciseSession>
}