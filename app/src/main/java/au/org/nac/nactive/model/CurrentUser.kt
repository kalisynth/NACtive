package au.org.nac.nactive.model

import com.chibatching.kotpref.KotprefModel

/**
 * Current User
 */
object CurrentUser : KotprefModel() {
    var name by stringPref()
}