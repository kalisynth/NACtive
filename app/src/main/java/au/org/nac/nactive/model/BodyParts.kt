package au.org.nac.nactive.model

/**
 * Body parts
 */
enum class BodyParts(val friendlyBodyPart: String) {
    LEFTARM("Left Arm"),
    RIGHTARM("Right Arm"),
    LEFTHAND("Left Hand"),
    RIGHTHAND("Right Hand"),
    LEFTFOOT("Left Foot"),
    RIGHTFOOT("Right Foot"),
    LEFTLEG("Left Leg"),
    RIGHTLEG("Right Leg"),
    TORSO("Torso"),
    HEAD("Head"),
    NECK("Neck"),
    SPINE("Spine"),
    DEFAULT("Whole Body");

    override fun toString(): String {
        return friendlyBodyPart
    }
}