package com.cyxbs.pages.login.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSetScope
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import com.cyxbs.pages.login.viewmodel.LoginViewModel

/**
 * 管理登录页在不同窗口宽高比例下的显示
 *
 * @author 985892345
 * @date 2025/1/5
 */
enum class Element {
  Title,
  SubTitle,
  StuNumPassword,
  UserAgreement,
  ForgetPassword,
  LoginBtn,
  TouristMode,
  LoginAnim,
}

@Stable
class LoginConstraintSet(
  val scope: ConstraintSetScope,
  val viewModel: LoginViewModel,
  val windowSize: DpSize,
) {
  val title = scope.createRefFor(Element.Title)
  val subTitle = scope.createRefFor(Element.SubTitle)
  val stuNumPassword = scope.createRefFor(Element.StuNumPassword)
  val userAgreement = scope.createRefFor(Element.UserAgreement)
  val forgetPassword = scope.createRefFor(Element.ForgetPassword)
  val loginBtn = scope.createRefFor(Element.LoginBtn)
  val touristMode = scope.createRefFor(Element.TouristMode)
  val loginAnim = scope.createRefFor(Element.LoginAnim)

  fun createConstrain() {
    loginAnimConstrain()
    val ratio = windowSize.height / windowSize.width
    when {
      ratio > 1.5F -> wh100vInfinity()
      ratio <= 1.5F -> wh100v150()
    }
  }
}

// 显示登录动画
private fun LoginConstraintSet.loginAnimConstrain() {
  val enableShow by viewModel.isLoginAnim
  scope.constrain(title) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(subTitle) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(stuNumPassword) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(userAgreement) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(forgetPassword) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(loginBtn) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(touristMode) { visibility = if (enableShow) Visibility.Gone else Visibility.Visible }
  scope.constrain(loginAnim) { visibility = if (enableShow) Visibility.Visible else Visibility.Gone }
}

// 宽高比 100:∞
private fun LoginConstraintSet.wh100vInfinity() {
  scope.constrain(title) {
    linkTo(parent.top, parent.bottom)
    verticalBias = 0.19F
    start.linkTo(parent.start, 16.dp)
  }
  scope.constrain(subTitle) {
    top.linkTo(title.bottom, 16.dp)
    start.linkTo(parent.start, 16.dp)
  }
  scope.constrain(stuNumPassword) {
    top.linkTo(subTitle.bottom, 16.dp)
    linkTo(parent.start, parent.end, 16.dp)
    width = Dimension.fillToConstraints
  }
  scope.constrain(userAgreement) {
    start.linkTo(stuNumPassword.start)
    top.linkTo(stuNumPassword.bottom, 8.dp)
  }
  scope.constrain(forgetPassword) {
    end.linkTo(stuNumPassword.end, 12.dp)
    centerVerticallyTo(userAgreement)
  }
  scope.constrain(loginBtn) {
    linkTo(parent.start, parent.end, 16.dp, 16.dp)
    linkTo(userAgreement.bottom, parent.bottom, 16.dp, 16.dp)
    verticalBias = 0.2F
    width = Dimension.preferredValue(300.dp)
  }
  scope.constrain(touristMode) {
    centerHorizontallyTo(parent)
    linkTo(loginBtn.bottom, parent.bottom)
    verticalBias = 0.85F
  }
}

// 宽高比 100:150
private fun LoginConstraintSet.wh100v150() {
  scope.constrain(title) {
    centerHorizontallyTo(parent)
    if (windowSize.height > 400.dp) {
      linkTo(parent.top, parent.bottom, bias = 0.15F)
    } else {
      // 手机横屏时
      top.linkTo(parent.top)
    }
  }
  scope.constrain(subTitle) {
    top.linkTo(title.bottom, 8.dp)
    centerHorizontallyTo(parent)
  }
  scope.constrain(stuNumPassword) {
    top.linkTo(subTitle.bottom, if (windowSize.height > 400.dp) 16.dp else 8.dp)
    centerHorizontallyTo(parent)
    width = Dimension.preferredValue(300.dp)
  }
  scope.constrain(userAgreement) {
    start.linkTo(stuNumPassword.start)
    top.linkTo(stuNumPassword.bottom, 8.dp)
  }
  scope.constrain(forgetPassword) {
    end.linkTo(stuNumPassword.end, 12.dp)
    centerVerticallyTo(userAgreement)
  }
  scope.constrain(loginBtn) {
    linkTo(parent.start, parent.end, 16.dp, 16.dp)
    linkTo(userAgreement.bottom, parent.bottom, 16.dp, 16.dp)
    verticalBias = 0.2F
    width = Dimension.preferredValue(300.dp)
  }
  scope.constrain(touristMode) {
    centerHorizontallyTo(parent)
    linkTo(loginBtn.bottom, parent.bottom)
    verticalBias = 0.85F
  }
}
