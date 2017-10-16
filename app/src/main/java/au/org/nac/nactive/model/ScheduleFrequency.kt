package au.org.nac.nactive.model

/**
 * Schedule Enum
 */
enum class ScheduleFrequency(val friendlyName: String) {
    ALWAYS("Always"),
    RANDOM("Random"),
    SCHEDULED("Scheduled"),
    UNKNOWN("Unknown");

    override fun toString(): String{
        return friendlyName
    }
}