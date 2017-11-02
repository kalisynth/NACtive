package au.org.nac.nactive.model
import io.objectbox.annotation.*

/**
 * User Model
 */

@Entity
data class User (
        @Id var id: Long = 0,
        //User Data
        var name: String? = "", //User Name
        var createdDate: String? = "",
        var nextWorkOutId: Long = 0,
        var currentWorkOutId: Long = 0,
        var previousWorkOutId: Long = 0,
        var userLevel: Int = 0,
        var userExperience: Int = 0,
        var googleUID: String? = "",
        var workOutScheduleString: String? = "")