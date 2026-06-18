package gov.utah.sedi

import androidx.compose.ui.graphics.Color

object SediBrand {
    const val APP_NAME = "SEDI"
    const val HUMAN_NAME = "Secure Identity"
    const val TAGLINE_PRIMARY = "Your identity. Your approval."
    const val TAGLINE_SECONDARY = "Share proof, not personal data."
    const val POSITIONING = "Approve identity sharing the way you approve app permissions."

    object Copy {
        const val APPROVED_ONLY = "Only approved proof is shared."
        const val PRIVATE_DETAILS = "Your private details stay hidden."
        const val REVOKE_ANYTIME = "You can remove access anytime."
        const val NOT_SHARED = "Not shared with institutions."
        const val IN_CONTROL = "You're in control."
        const val REVIEW_BEFORE = "Review before sharing."
    }

    // Cinematic identity palette
    val Midnight = Color(0xFF0A121C)
    val Charcoal = Color(0xFF121C28)
    val Graphite = Color(0xFF1A2634)
    val Navy = Color(0xFF1E3044)
    val Steel = Color(0xFF6B7A88)
    val MistText = Color(0xFFE8EDF2)
    val Teal = Color(0xFF4A9BB5)
    val TealSoft = Color(0xFF7CB4C8)
    val TealGlow = Color(0xFF5BA8BE)
    val SurfaceWarm = Color(0xFFF7F5F2)
    val SurfaceElevated = Color(0xFFFFFFFF)
    val Ink = Color(0xFF152433)
    val Slate = Color(0xFF5C6B78)
    val Success = Color(0xFF3D9A78)
    val SuccessSoft = Color(0xFF5BB896)
}
