# Sofascore Patches (DutchPharaoh)

[Morphe](https://morphe.software) patches for **Sofascore 26.11.09** (version code 261109002) and **26.11.03** (version codes 261103002 and 261103003), package `com.sofascore.results`.

## Patches

| Patch | Description |
|---|---|
| **Show team logos in full standings** | In *Standings*, the compact view shows the club logo before the team name, but the full view (P, W, D, L, goals, points) and the form view drop it. Sofascore only shows the logo there when the table is at least 580dp wide, which in practice means tablets. This patch lowers that limit to 0, so the logo is always shown. The header row is adjusted the same way, so the columns stay lined up. |
| **Show goal difference in full standings** | Adds the goal difference column (*Diff*) after the goals column in the full view, as in the compact view. Sofascore drops it there for football (it only keeps it for handball, rugby, water polo, basketball and baseball). The column is only shown when the table has goal-difference data; the header row uses the same column list. The values shrink their text (8–12sp) to fit, so the table does not scroll sideways. Column titles are kept on one line, so a title such as *PTS* shrinks a little instead of wrapping to *PT / S*. |

### How it works

Both the row binder (`wr9.q` in 26.11.03, `uy8.J` in 26.11.09) and the header binder (`wr9.p`, `uy8.I`) compare the table width with `dp(580)`. The patch changes the constant `580` into `0` in those two methods only; it is used for nothing else there. The methods are found by their shape (static, parameter types), the `580` literal and a nearby string (`"OTL"` for the row, `"#"` for the header), and the patch fails if the `580` literal is not found exactly once.

For the goal difference, the patch removes the `column == SCORE_DIFF -> skip` branch in the method that builds the column lists (`ny8.invokeSuspend` in 26.11.03, `nx8.invokeSuspend` in 26.11.09), found by its `Set.contains` / `if-eq` instruction pattern and the strings `"afc|nfc|american|national"` and `" - "`.

On a very narrow screen with many columns, the logo takes 32dp from the team name; the team name keeps its minimum width of 108dp.

## Use

1. Download Sofascore **26.11.09** (or 26.11.03) from APKMirror (any variant, it is an `.apkm` bundle).
2. In Morphe Manager: *Sources → + → Remote*, enter `github.com/DutchPharaoh/SofaScore`.
3. Patch Sofascore with *Show team logos in full standings* and *Show goal difference in full standings*.

## Build

```
./gradlew :patches:buildAndroid
```

Needs a GitHub token with `read:packages` (`GITHUB_ACTOR`/`GITHUB_TOKEN` or `gpr.user`/`gpr.key`) for the Morphe Gradle plugin. GitHub Actions (`.github/workflows/build.yml`) builds the `.mpp` on every push and publishes a release from `main`.

Based on the [Morphe patches template](https://github.com/MorpheApp/morphe-patches-template) (GPLv3).
