package app.dutchpharaoh.sofascore.patches.standings

import app.dutchpharaoh.sofascore.patches.shared.Constants.COMPATIBILITY_SOFASCORE
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch

@Suppress("unused")
val showGoalDifferenceInFullStandingsPatch = bytecodePatch(
    name = "Show goal difference in full standings",
    description = "Adds the goal difference column (Diff) after the goals column in the full standings view, " +
        "as in the compact view. Sofascore normally drops it there for football.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SOFASCORE)

    execute {
        // Drop the "column == SCORE_DIFF -> skip" branch. The column is still only shown when the
        // table has goal-difference data, and the header row uses the same column list.
        val ifEq = StandingsColumnsFingerprint.instructionMatches.last()
        StandingsColumnsFingerprint.method.replaceInstruction(ifEq.index, "nop")
    }
}
