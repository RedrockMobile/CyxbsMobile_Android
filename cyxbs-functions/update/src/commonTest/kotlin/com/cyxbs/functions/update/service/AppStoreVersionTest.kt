package com.cyxbs.functions.update.service

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppStoreVersionTest {
  @Test
  fun comparesNumericComponentsInsteadOfText() {
    assertTrue(isNewerAppStoreVersion("6.10.0", "6.9.9"))
    assertFalse(isNewerAppStoreVersion("6.9.9", "6.10.0"))
    assertTrue(isNewerAppStoreVersion("7.0", "6.99.99"))
    assertTrue(isNewerAppStoreVersion("6.6.5", "6.6.4"))
  }

  @Test
  fun treatsTrailingZeroComponentsAsEquivalent() {
    assertFalse(isNewerAppStoreVersion("6.6.0", "6.6"))
    assertFalse(isNewerAppStoreVersion("6.6", "6.6.0"))
    assertFalse(isNewerAppStoreVersion("6.6.4", "6.6.4"))
  }

  @Test
  fun doesNotRecommendDowngradingDevelopmentBuilds() {
    assertFalse(isNewerAppStoreVersion("6.6.4", "6.7.0"))
  }

  @Test
  fun invalidVersionsFailInsteadOfReportingUpToDate() {
    assertFailsWith<NumberFormatException> { isNewerAppStoreVersion("unknown", "6.6.4") }
    assertFailsWith<NumberFormatException> { isNewerAppStoreVersion("6.6.4", "") }
  }
}
