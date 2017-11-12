package au.org.nac.nactive.model
import io.objectbox.annotation.*
import io.objectbox.relation.ToOne


/**
 * Work Out Stats Model
 */

@Entity
data class WorkOutStats (
    @Id var id: Long = 0,
    val time: Long = 0,
    val completed: Boolean = false,
    val exerciseIdStoppedOn : String? = "",
    val workOutId: Long = 0,
    val workOutFocus: String = ""
)