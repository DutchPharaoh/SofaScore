package app.dutchpharaoh.sofascore.patches.standings

import app.dutchpharaoh.sofascore.patches.shared.Constants.COMPATIBILITY_SOFASCORE
import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction

@Suppress("unused")
val showTeamLogosInFullStandingsPatch = bytecodePatch(
    name = "Show team logos in full standings",
    description = "Shows the team logo in front of the team name in the full and form standings views, " +
        "as in the compact view. Sofascore normally only does this on screens of at least 580dp wide (tablets).",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SOFASCORE)

    execute {
        // Lower the 580dp width threshold to 0dp, so "width >= threshold" is always true.
        // In both methods the constant is used for nothing else.
        listOf(StandingsRowFingerprint, StandingsHeaderFingerprint).forEach { it.removeWidthThreshold() }
    }
}

private fun Fingerprint.removeWidthThreshold() {
    val literals = method.implementation!!.instructions.count {
        (it as? WideLiteralInstruction)?.wideLiteral == LOGO_MIN_WIDTH_DP.toLong()
    }
    if (literals != 1) {
        throw PatchException("Expected one $LOGO_MIN_WIDTH_DP literal in $method, found $literals")
    }

    val match = instructionMatches.first()
    val register = match.getInstruction<OneRegisterInstruction>().registerA
    method.replaceInstruction(match.index, "const/16 v$register, 0x0")
}
