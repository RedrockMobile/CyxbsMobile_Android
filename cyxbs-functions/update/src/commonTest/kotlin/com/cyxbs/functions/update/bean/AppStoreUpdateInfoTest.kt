package com.cyxbs.functions.update.bean

import com.cyxbs.components.config.serializable.defaultJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppStoreUpdateInfoTest {
  @Test
  fun decodesAppleLookupPayloadAndKeepsReleaseNotes() {
    val json = """
      {"resultCount":1,"results":[{
        "trackId":974026615,"bundleId":"com.mredrock.cyxbs","version":"6.6.4",
        "trackViewUrl":"https://apps.apple.com/cn/app/id974026615?uo=4",
        "releaseNotes":"修复课表\n完善暗黑模式","minimumOsVersion":"15.0",
        "trackName":"掌上重邮","unknownField":true
      }]}
    """.trimIndent()
    val info = defaultJson.decodeFromString<AppStoreLookupResult>(json).toUpdateInfo()
    assertEquals("6.6.4", info.versionName)
    assertEquals("https://apps.apple.com/cn/app/id974026615?uo=4", info.apkUrl)
    assertTrue(info.updateContent.startsWith("修复课表\n完善暗黑模式"))
    assertTrue(info.updateContent.contains("iOS 15.0"))
  }

  @Test
  fun emptyOrMismatchedResultsFail() {
    assertFailsWith<IllegalStateException> {
      defaultJson.decodeFromString<AppStoreLookupResult>("""{"resultCount":0,"results":[]}""").toUpdateInfo()
    }
    assertFailsWith<IllegalStateException> {
      AppStoreLookupResult(listOf(app.copy(bundleId = "another.app"))).toUpdateInfo()
    }
    assertFailsWith<IllegalStateException> {
      AppStoreLookupResult(listOf(app.copy(trackId = 1))).toUpdateInfo()
    }
  }

  @Test
  fun missingNotesHaveAnExplicitMessage() {
    assertEquals("暂无更新说明", AppStoreLookupResult(listOf(app)).toUpdateInfo().updateContent)
  }

  @Test
  fun rejectsUnexpectedStoreLinksAndMissingVersion() {
    assertFailsWith<IllegalArgumentException> {
      AppStoreLookupResult(listOf(app.copy(trackViewUrl = "https://example.com/app"))).toUpdateInfo()
    }
    assertFailsWith<IllegalArgumentException> {
      AppStoreLookupResult(listOf(app.copy(version = ""))).toUpdateInfo()
    }
  }

  private val app = AppStoreUpdateInfo(
    trackId = APP_STORE_ID,
    bundleId = APP_STORE_BUNDLE_ID,
    version = "6.6.4",
    trackViewUrl = "https://apps.apple.com/cn/app/id974026615",
  )
}
