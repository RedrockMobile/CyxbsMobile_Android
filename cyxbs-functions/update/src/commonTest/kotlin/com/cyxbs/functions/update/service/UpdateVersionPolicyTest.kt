package com.cyxbs.functions.update.service

import com.cyxbs.components.config.Platform
import com.cyxbs.functions.update.api.UpdateInfo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdateVersionPolicyTest {
  @Test
  fun iosSameVersionIsValidEvenWithLargerRemoteBuildNumber() {
    assertFalse(isUpdateAvailable(Platform.IOS, UpdateInfo(versionName = "6.6.4", versionCode = 999), "6.6.4", 4))
  }

  @Test
  fun iosOnlyOffersStrictlyNewerRemoteVersion() {
    assertTrue(isUpdateAvailable(Platform.IOS, UpdateInfo(versionName = "6.6.5"), "6.6.4", 999))
    assertFalse(isUpdateAvailable(Platform.IOS, UpdateInfo(versionName = "6.6.3"), "6.6.4", 4))
    assertFalse(isUpdateAvailable(Platform.IOS, UpdateInfo(versionName = "6.6.4.0"), "6.6.4", 4))
  }

  @Test
  fun androidStillUsesBuildNumber() {
    val remote = UpdateInfo(versionName = "6.6.4", versionCode = 94)
    assertTrue(isUpdateAvailable(Platform.Android, remote, "6.7.0", 93))
    assertFalse(isUpdateAvailable(Platform.Android, remote, "6.6.4", 94))
    assertFalse(isUpdateAvailable(Platform.Android, remote, "6.7.0", 95))
    assertTrue(isUpdateAvailable(Platform.Android, remote, "6.6.4-alpha", 94))
  }
}
