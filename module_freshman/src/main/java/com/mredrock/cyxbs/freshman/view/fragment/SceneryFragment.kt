package com.mredrock.cyxbs.freshman.view.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.freshman.R
import com.mredrock.cyxbs.freshman.base.BaseFragment
import com.mredrock.cyxbs.freshman.bean.Photo
import com.mredrock.cyxbs.freshman.bean.Scenery
import com.mredrock.cyxbs.freshman.config.API_BASE_IMG_URL
import com.mredrock.cyxbs.freshman.interfaces.model.IFragmentSceneryModel
import com.mredrock.cyxbs.freshman.interfaces.presenter.IFragmentSceneryPresenter
import com.mredrock.cyxbs.freshman.interfaces.view.IFragmentSceneryView
import com.mredrock.cyxbs.freshman.presenter.FragmentSceneryPresenter
import com.mredrock.cyxbs.freshman.view.activity.showPhotosToMap
import com.mredrock.cyxbs.freshman.view.activity.showPhotosToScenery
import com.mredrock.cyxbs.freshman.view.adapter.OnItemClickListener
import com.mredrock.cyxbs.freshman.view.adapter.SceneryRecyclerViewAdapter
import org.jetbrains.anko.find

/**
 * Create by roger
 * on 2019/8/3
 */
class SceneryFragment :
        BaseFragment<IFragmentSceneryView, IFragmentSceneryPresenter, IFragmentSceneryModel>(), IFragmentSceneryView {
    private lateinit var recyclerView: RecyclerView

    override fun setPhotos(scenery: Scenery) {
        val list = mutableListOf<Photo>()
        list.add(Photo(scenery.title, "$API_BASE_IMG_URL${scenery.photo}"))
        list.addAll(scenery.photos)
        val adapter = SceneryRecyclerViewAdapter(context!!, list)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position == 0) {
                    val url = "$API_BASE_IMG_URL${scenery.photo}"
                    activity?.let { showPhotosToMap(it, listOf(url)) }
                } else {
                    val list2 = ArrayList<String>()
                    for (x in scenery.photos) {
                        list2.add(x.photo)
                    }
                    activity?.let { showPhotosToScenery(it, list2, position - 1) }
                }
            }

        })
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                when ((view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition) {
                    (parent.adapter?.itemCount!!.minus(1)) -> outRect.bottom = dp2px(50)
                }
            }
        })
    }

    override fun showError() {
    }

    override fun onCreateView(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.find(R.id.rv_scenery)
        val grid = GridLayoutManager(context, 2)
        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) {
                    2
                } else {
                    1
                }
            }
        }
        recyclerView.layoutManager = grid

        presenter?.start()


    }

    override fun getLayoutRes(): Int {
        return R.layout.freshman_fragment_scenery
    }

    override fun getViewToAttach(): IFragmentSceneryView {
        return this

    }

    override fun createPresenter(): IFragmentSceneryPresenter {
        return FragmentSceneryPresenter()
    }

    fun dp2px(value: Int): Int {
        val v = BaseApp.context.resources.displayMetrics.density
        return (v * value + 0.5f).toInt()
    }
}