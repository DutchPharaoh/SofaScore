package app.dutchpharaoh.sofascore.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_SOFASCORE = Compatibility(
        name = "Sofascore",
        packageName = "com.sofascore.results",
        // APKMirror only offers 26.11.03 as a bundle (.apkm).
        apkFileType = ApkFileType.APKM,
        appIconColor = 0x374DF5,
        targets = listOf(
            AppTarget(version = "26.11.03"),
        ),
    )
}
