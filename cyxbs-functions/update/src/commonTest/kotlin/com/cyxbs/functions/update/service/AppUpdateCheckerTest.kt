package com.cyxbs.functions.update.service

import com.cyxbs.functions.update.api.AppUpdateStatus
import com.cyxbs.functions.update.api.UpdateInfo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class AppUpdateCheckerTest {
  @Test
  fun missingInstalledVersionProducesErrorInsteadOfUpToDate() = runTest {
    val checker = AppUpdateChecker(backgroundScope, { info }) {
      isNewerAppStoreVersion(it.versionName, "")
    }
    assertIs<AppUpdateStatus.Result.Error>(checker.checkUpdate())
    assertNull(checker.info.value)
  }

  @Test
  fun previewThenNormalClickDoesNotTurnEqualVersionIntoUpdate() = runTest {
    for (platform in listOf(com.cyxbs.components.config.Platform.IOS, com.cyxbs.components.config.Platform.Android)) {
      val checker = AppUpdateChecker(backgroundScope, { info }) {
        isUpdateAvailable(platform, it, "6.6.4", info.versionCode)
      }
      assertEquals(info, checker.checkPreviewInfo())
      assertSame(AppUpdateStatus.Result.Valid, checker.status.value)
      assertSame(AppUpdateStatus.Result.Valid, checker.checkUpdate())
    }
  }

  @Test
  fun failedPreviewDoesNotReuseOldStoreInfo() = runTest {
    var fail = false
    val checker = AppUpdateChecker(backgroundScope, {
      if (fail) error("offline") else info
    }, { false })
    checker.checkUpdate()
    fail = true
    assertNull(checker.checkPreviewInfo())
    assertIs<AppUpdateStatus.Result.Error>(checker.status.value)
  }

  @Test
  fun previewKeepsItsResponseWhenAnotherCheckFinishesBeforeItResumes() = runTest {
    val firstResponse = CompletableDeferred<UpdateInfo>()
    val nextInfo = info.copy(versionName = "6.6.5")
    var requests = 0
    val requestScope = CoroutineScope(backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler))
    val checker = AppUpdateChecker(requestScope, {
      if (++requests == 1) firstResponse.await() else nextInfo
    }, { false })
    val preview = async { checker.checkPreviewInfo() }
    runCurrent()

    // 请求先完成，预览调用者仍在排队恢复；此时另一次检查刷新了公共状态。
    firstResponse.complete(info)
    val refresh = async(start = CoroutineStart.UNDISPATCHED) { checker.checkUpdate() }
    assertSame(AppUpdateStatus.Result.Valid, refresh.await())
    assertEquals(nextInfo, checker.info.value)
    assertEquals(info, preview.await())
  }

  @Test
  fun automaticAndManualChecksShareOneRequest() = runTest {
    val response = CompletableDeferred<UpdateInfo>()
    var requests = 0
    val checker = AppUpdateChecker(backgroundScope, { requests++; response.await() }, { true })
    val automatic = async { checker.checkUpdate() }
    val manual = async { checker.checkUpdate() }
    runCurrent()
    assertEquals(1, requests)
    assertSame(AppUpdateStatus.Checking, checker.status.value)

    response.complete(info)
    assertIs<AppUpdateStatus.Result.Dated>(automatic.await())
    assertEquals(automatic.await(), manual.await())
    assertEquals(info, checker.info.value)
  }

  @Test
  fun leavingPageDoesNotCancelSharedRequestOrStrandCheckingState() = runTest {
    val response = CompletableDeferred<UpdateInfo>()
    val checker = AppUpdateChecker(backgroundScope, { response.await() }, { false })
    val caller = async { checker.checkUpdate() }
    runCurrent()
    caller.cancelAndJoin()
    response.complete(info)
    runCurrent()
    assertSame(AppUpdateStatus.Result.Valid, checker.status.value)
    assertEquals(info, checker.info.value)
  }

  @Test
  fun keepsNotesWhenUpToDateAndRefreshesPreviouslyOutdatedResult() = runTest {
    var installed = "6.6.3"
    var requests = 0
    val checker = AppUpdateChecker(backgroundScope, { requests++; info }) {
      isNewerAppStoreVersion(it.versionName, installed)
    }
    assertIs<AppUpdateStatus.Result.Dated>(checker.checkUpdate())
    installed = "6.6.4"
    assertSame(AppUpdateStatus.Result.Valid, checker.checkUpdate())
    assertEquals(2, requests)
    assertEquals(info, checker.info.value)
  }

  @Test
  fun failureClearsOldInfoAndCanBeRetried() = runTest {
    var fail = false
    val checker = AppUpdateChecker(backgroundScope, {
      if (fail) error("network unavailable") else info
    }, { false })
    checker.checkUpdate()
    fail = true
    assertIs<AppUpdateStatus.Result.Error>(checker.checkUpdate())
    assertNull(checker.info.value)
    fail = false
    assertSame(AppUpdateStatus.Result.Valid, checker.checkUpdate())
    assertEquals(info, checker.info.value)
  }

  @Test
  fun canceledRequestDoesNotLeaveCheckingState() = runTest {
    var cancel = true
    val checker = AppUpdateChecker(backgroundScope, {
      if (cancel) throw CancellationException("request canceled") else info
    }, { false })
    assertFailsWith<CancellationException> { checker.checkUpdate() }
    assertIs<AppUpdateStatus.Result.Error>(checker.status.value)
    cancel = false
    assertSame(AppUpdateStatus.Result.Valid, checker.checkUpdate())
  }

  private val info = UpdateInfo(
    apkUrl = "https://apps.apple.com/cn/app/id974026615",
    versionName = "6.6.4",
    updateContent = "修复课表",
  )
}
