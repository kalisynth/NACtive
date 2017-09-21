package au.org.nac.nactive.Models

import io.requery.*
import java.util.*

/**
 * User Model
 */

@Entity
interface User : Persistable {
    @get:Key
    @get:Generated
    var id: Int
    val name: String
    val nextSession: String
    val currentSession: String
    val previousSession: String

    @get:Column(unique = true)
    var uuid: UUID

    @get:OneToMany(mappedBy = "user")
    val exerciseSessions: Set<ExerciseSession>
}