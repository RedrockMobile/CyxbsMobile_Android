package com.mredrock.cyxbs.noclass.util

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mredrock.cyxbs.lib.base.utils.ArgumentHelper

open class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    fun <T : Any> arguments() = ArgumentHelper<T>{ requireArguments() }
}