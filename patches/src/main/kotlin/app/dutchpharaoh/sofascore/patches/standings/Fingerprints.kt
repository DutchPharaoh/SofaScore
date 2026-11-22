package app.dutchpharaoh.sofascore.patches.standings

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.fieldAccess
import app.morphe.patcher.literal
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

/**
 * Minimum table width (in dp) Sofascore requires before it shows team logos
 * in the "full" and "form" standings views. Converted to pixels right after.
 */
internal const val LOGO_MIN_WIDTH_DP = 580

/**
 * Binds one team row of a standings table (`standings_cell` layout).
 *
 * 26.11.03: `wr9.q(gul, uvl, avl, List, Map, boolean)`. In the full and form views it does
 * `teamLogo.setVisibility(widthWrapper.width >= dp(580) ? VISIBLE : GONE)`.
 * The compact view always shows the logo.
 */
internal object StandingsRowFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "L", "L", "Ljava/util/List;", "Ljava/util/Map;", "Z"),
    filters = listOf(
        literal(LOGO_MIN_WIDTH_DP),
        // Form column value for "overtime loss" (ice hockey).
        string("OTL"),
    ),
)

/**
 * Binds the header row of a standings table (same `standings_cell` layout).
 *
 * 26.11.03: `wr9.p(gul, pvl, avl, List, boolean)`. It widens the team-name column by 32dp
 * (the space of the logo) only when the table is at least 580dp wide, so the header columns
 * line up with the rows.
 */
internal object StandingsHeaderFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL),
    returnType = "V",
    parameters = listOf("L", "L", "L", "Ljava/util/List;", "Z"),
    filters = listOf(
        literal(LOGO_MIN_WIDTH_DP),
        // Rank column title of the header row.
        string("#"),
    ),
)

/**
 * Builds the column lists of a standings table.
 *
 * 26.11.03: `ny8.invokeSuspend`. The full view starts from all standings columns and filters out
 * the ones that do not belong to the sport. One of those filters is
 * `if (!sportsWithDiffInFullView.contains(sport) && column == SCORE_DIFF) skip`, where that set
 * is handball, rugby, water polo, basketball and baseball. Football therefore loses its goal
 * difference ("Diff") column in the full view, while the compact view keeps it.
 */
internal object StandingsColumnsFingerprint : Fingerprint(
    name = "invokeSuspend",
    returnType = "Ljava/lang/Object;",
    parameters = listOf("Ljava/lang/Object;"),
    strings = listOf("afc|nfc|american|national", " - "),
    filters = listOf(
        fieldAccess(opcode = Opcode.SGET_OBJECT, type = "Ljava/util/LinkedHashSet;"),
        methodCall(definingClass = "Ljava/util/Set;", name = "contains", location = MatchAfterImmediately()),
        opcode(Opcode.MOVE_RESULT, MatchAfterImmediately()),
        opcode(Opcode.IF_NEZ, MatchAfterImmediately()),
        // SCORE_DIFF column.
        opcode(Opcode.SGET_OBJECT, MatchAfterImmediately()),
        // column == SCORE_DIFF -> skip it.
        opcode(Opcode.IF_EQ, MatchAfterImmediately()),
    ),
)
