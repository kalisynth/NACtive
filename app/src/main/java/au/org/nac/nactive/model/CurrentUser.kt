package au.org.nac.nactive.model

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.enumpref.enumValuePref

/**
 * Current User
 */
object CurrentUser : KotprefModel() {
    var name by stringPref()
    var userId by longPref()
    var isGoogleUser by booleanPref()
    var googleUUID by stringPref()
    var freq by enumValuePref(ScheduleFrequency.ALWAYS)
}