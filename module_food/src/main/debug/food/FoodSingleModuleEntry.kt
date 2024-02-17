package food

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.food.ui.activity.FoodMainActivity
import com.mredrock.cyxbs.single.ISingleModuleEntry
import com.mredrock.cyxbs.single.ui.SingleModuleActivity

/**
 * .
 *
 * @author 985892345
 * @date 2024/2/15 14:01
 */
@Route(path = "/single/food")
class FoodSingleModuleEntry : ISingleModuleEntry {
  override fun getPage(activity: SingleModuleActivity): ISingleModuleEntry.Page {
    return ISingleModuleEntry.ActionPage {
      startActivity(Intent(this, FoodMainActivity::class.java))
    }
  }
}