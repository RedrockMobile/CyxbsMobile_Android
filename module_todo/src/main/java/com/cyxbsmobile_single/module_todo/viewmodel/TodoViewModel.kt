import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel

/**
 * @Project: CyxbsMobile_Android
 * @File: TodoViewModel
 * @Author: 86199
 * @Date: 2024/8/16
 * @Description: 用来管理数据
 */
class TodoViewModel:BaseViewModel() {
    private val isclick =false
    private val _isEnabled = MutableLiveData<Boolean>()
    val isEnabled: LiveData<Boolean> get() = _isEnabled

    fun setEnabled() {
        _isEnabled.value = !isclick
        Log.d("TodoViewModel", "isEnabled set to ${_isEnabled.value}")
    }
}