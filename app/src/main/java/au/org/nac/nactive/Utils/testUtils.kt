package au.org.nac.nactive.Utils

import java.util.Arrays

/**
 * Created by Cade2 on 23/10/2017.
 */

class testUtils {

    fun returnStepsList(steps: String): List<String> {
        return Arrays.asList(*steps.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }
}
