package app.dutchpharaoh.sofascore.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_SOFASCORE = Compatibility(
        name = "Sofascore",
        packageName = "com.sofascore.results",
        // APKMirror only offers these versions as bundles (.apkm).
        apkFileType = ApkFileType.APKM,
        appIconColor = 0x374DF5,
        targets = listOf(
            AppTarget(version = "26.11.09"),
            AppTarget(version = "26.11.03"),
        ),
    )
}
