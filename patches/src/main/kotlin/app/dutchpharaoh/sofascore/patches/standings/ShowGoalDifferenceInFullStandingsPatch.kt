package app.dutchpharaoh.sofascore.patches.standings

import app.dutchpharaoh.sofascore.patches.shared.Constants.COMPATIBILITY_SOFASCORE
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val showGoalDifferenceInFullStandingsPatch = bytecodePatch(
    name = "Show goal difference in full standings",
    description = "Adds the goal difference column (Diff) after the goals column in the full standings view, " +
        "as in the compact view. Sofascore normally drops it there for football. " +
        "Column titles are kept on one line, so they shrink instead of wrapping (\"PT / S\").",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SOFASCORE)

    execute {
        // 1. Drop the "column == SCORE_DIFF -> skip" branch. The column is still only shown when the
        //    table has goal-difference data, and the header row uses the same column list.
        val ifEq = StandingsColumnsFingerprint.instructionMatches.last()
        StandingsColumnsFingerprint.method.replaceInstruction(ifEq.index, "nop")

        // 2. With one column more, a title such as "PTS" can be too wide for its column. The header
        //    titles may use two lines (layout maxLines=2) and auto-size between 8 and 12sp, so
        //    Android wraps the title before it shrinks it. Keep the titles on one line, so they shrink.
        //    Header binder: `c9d.v(TextView, 8, 12)` is its only (TextView, int, int) static call.
        val header = StandingsHeaderFingerprint.method
        val calls = header.implementation!!.instructions.withIndex().filter { (_, insn) ->
            val ref = (insn as? ReferenceInstruction)?.reference as? MethodReference
            insn.opcode == Opcode.INVOKE_STATIC && ref != null && ref.returnType == "V" &&
                ref.parameterTypes.map { it.toString() } == listOf("Landroid/widget/TextView;", "I", "I")
        }
        if (calls.size != 1) throw PatchException("Expected one auto-size call in $header, found ${calls.size}")

        val (index, insn) = calls.single()
        val call = insn as FiveRegisterInstruction
        val textView = call.registerC
        // The max-size argument register only holds a constant for this call; it is reassigned
        // before every later read, so it can be reused for the constant 1.
        val temp = call.registerE
        header.addInstructions(
            index + 1,
            """
                const/4 v$temp, 0x1
                invoke-virtual {v$textView, v$temp}, Landroid/widget/TextView;->setMaxLines(I)V
            """,
        )
    }
}
