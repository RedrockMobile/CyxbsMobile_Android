package com.mredrock.cyxbs.discover.map.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.component.MapToast
import com.mredrock.cyxbs.discover.map.databinding.MapFragmentFavoriteEditBinding
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.mredrock.cyxbs.discover.map.widget.MapDialog
import com.mredrock.cyxbs.discover.map.widget.OnSelectListener
import com.mredrock.cyxbs.discover.map.widget.ProgressDialog
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.discover.map.component.FavoriteEditText


class FavoriteEditFragment : BaseFragment() {
    private lateinit var viewModel: MapViewModel
    private lateinit var mBinding: MapFragmentFavoriteEditBinding

    private val mTvFavoriteCancel by R.id.map_tv_favorite_cancel.view<TextView>()
    private val mTvFavoriteCancelFavorite by R.id.map_tv_favorite_cancel_favorite.view<TextView>()
    private val mTvFavoriteAccept by R.id.map_tv_favorite_accept.view<TextView>()
    private val mEtFavoriteNickname by R.id.map_et_favorite_nickname.view<FavoriteEditText>()
    private val mTvFavoritePlaceName by R.id.map_tv_favorite_place_name.view<TextView>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.map_fragment_favorite_edit, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)
        mTvFavoriteCancel.setOnSingleClickListener {
            viewModel.fragmentFavoriteEditIsShowing.value = false
        }



        mTvFavoriteCancelFavorite.setOnSingleClickListener {
            context?.let { it1 ->
                MapDialog.show(it1, resources.getString(R.string.map_favorite_delete_title), resources.getString(R.string.map_favorite_delete), object : OnSelectListener {
                    override fun onDeny() {
                    }

                    override fun onPositive() {
                        viewModel.fragmentFavoriteEditIsShowing.value = false
                        viewModel.deleteCollect(viewModel.showingPlaceId)
                        ProgressDialog.show(requireActivity(), getString(R.string.map_please_a_moment_text), getString(R.string.map_collect_cancelling), false)

                    }
                })
            }
        }
        mTvFavoriteAccept.setOnSingleClickListener {
            if (mEtFavoriteNickname.length() != 0) {
                viewModel.fragmentFavoriteEditIsShowing.value = false
                ProgressDialog.show(requireActivity(), getString(R.string.map_please_a_moment_text), getString(R.string.map_collect_adding), false)

                viewModel.addCollect(mEtFavoriteNickname.text.toString(), viewModel.showingPlaceId)
            } else {
                MapToast.makeText(requireContext(), R.string.map_favorite_edit_length_not_enough, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        mEtFavoriteNickname.maxStringLength = 12
        if (viewModel.collectList.value != null) {
            var s = viewModel.placeDetails.value?.placeName
            for (t in viewModel.collectList.value!!) {
                if (t.placeId == viewModel.showingPlaceId) {
                    s = t.placeNickname
                    break
                }
            }
            mEtFavoriteNickname.setText(s)
        } else {
            mEtFavoriteNickname.setText(viewModel.placeDetails.value?.placeName)
        }

        mEtFavoriteNickname.notifyStringChange()
        mTvFavoritePlaceName.text = viewModel.placeDetails.value?.placeName
    }

}