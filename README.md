# Sofascore Patches (DutchPharaoh)

[Morphe](https://morphe.software) patches for **Sofascore 26.11.03** (`com.sofascore.results`, version codes 261103002 and 261103003).

## Patches

| Patch | Description |
|---|---|
| **Show team logos in full standings** | In *Standings*, the compact view shows the club logo before the team name, but the full view (P, W, D, L, goals, points) and the form view drop it. Sofascore only shows the logo there when the table is at least 580dp wide, which in practice means tablets. This patch lowers that limit to 0, so the logo is always shown. The header row is adjusted the same way, so the columns stay lined up. |

### How it works

Both the row binder (`wr9.q` in 26.11.03) and the header binder (`wr9.p`) compare the table width with `dp(580)`. The patch changes the constant `580` into `0` in those two methods only; it is used for nothing else there. The methods are found by their shape (static, parameter types), the `580` literal and a nearby string (`"OTL"` for the row, `"#"` for the header), and the patch fails if the `580` literal is not found exactly once.

On a very narrow screen with many columns, the logo takes 32dp from the team name; the team name keeps its minimum width of 108dp.

## Use

1. Download Sofascore **26.11.03** from APKMirror (any variant, it is an `.apkm` bundle).
2. In Morphe Manager: *Sources → + → Remote*, enter `github.com/DutchPharaoh/SofaScore`.
3. Patch Sofascore with *Show team logos in full standings*.

## Build

```
./gradlew :patches:buildAndroid
```

Needs a GitHub token with `read:packages` (`GITHUB_ACTOR`/`GITHUB_TOKEN` or `gpr.user`/`gpr.key`) for the Morphe Gradle plugin. GitHub Actions (`.github/workflows/build.yml`) builds the `.mpp` on every push and publishes a release from `main`.

Based on the [Morphe patches template](https://github.com/MorpheApp/morphe-patches-template) (GPLv3).
