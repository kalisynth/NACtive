package au.org.nac.nactive.model

import android.os.Parcelable
import io.requery.*
import java.util.*

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
    var isGoogleUser: Boolean

    var sodium: String

    @get:OneToMany(mappedBy = "user")
    val exerciseSessions : MutableSet<ExerciseSession>
}