package com.mredrock.cyxbs.store.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_ID

/**
 * 实现像数组一样的直接添加不同类型的 item, 使用方法可以点击查看使用实例(最好看我本人写的界面)
 *
 * **WARNING:** 必须使用 [show] 方法来开始加载
 *
 * **NOTE:** 没显示? 可能是你 RecyclerView 的 layoutManager 没设置。。。。。。
 * ```
 * 本 Adapter 的实现涉及到了:
 * 1、泛型擦除的解决(如何同时绑定多个不同类型的 DataBinding 或 ViewHolder)
 * 2、官方推荐的 DiffUtil 刷新帮助类
 * 3、带有三个参数的 onBindViewHolder 的使用
 *
 * 样例:
 * mTitleItem = TitleItem(mTitleMap) // 用来显示标题
 * mDataItem = DataItem(mDataMap)    // 用来显示每个标题下的数据
 * mRecyclerView.adapter = SimpleRvAdapter()
 *     .addItem(mTitleItem)
 *     .addItem(mDataItem)
 *     .show()
 *
 * 差分刷新使用方式:
 * mTitleItem.diffRefreshAllItemMap(mNewTitleMap,  // 传入新的键值对数据
 *     isSameName = { oldData, newData ->
 *         oldData.id == newData.id  // 比对两个数据类的唯一 id (可以去看我方法中的注释)
 *     },
 *     isSameData = { oldData, newData ->
 *         oldData == newData        // 比对两个数据类所有数据是否完全相同
 *     }
 * )
 * mDataItem 与上面一样, 不做演示
 * ```
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @date 2021/5/31 (在开发邮票商城项目前在自己的项目中开发的, 后续进行了许多优化和修改)
 * 开发邮票商城项目时间: 2021-8
 */
class SimpleRvAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 点击传入的类查看注解
     *
     * **WARNING:** 使用后记得使用 [show] 方法来开始加载
     * ```
     * 给方法传入一个泛型, 这个泛型的生命周期只能在方法内, 但我把泛型给了一个全局的对象, 则该泛型生命周期提高至全局
     * ```
     */
    fun <DB: ViewDataBinding, T> addItem(
        dataBindingItem: DBItem<DB, T>
    ): SimpleRvAdapter {
        dataBindingItem.adapter = this
        val call = BindingCallBack(dataBindingItem)
        // 一个 for 循环用于遍历全部 item 数量调用 isInHere 回调，添加进 mPositionsWithCallback 数组
        // 向上转型存进数组(能够解决泛型擦除的主要原因并不在这里,主要原因在于接口回调的强转写法)
        mLayoutIdWithCallback[dataBindingItem.layoutId] = call
        return this
    }

    /**
     * 点击传入的类查看注解
     *
     * **WARNING:** 使用后记得使用 [show] 方法来开始加载
     */
    fun <VH: RecyclerView.ViewHolder, T> addItem(
        viewHolderItem: VHItem<VH, T>
    ): SimpleRvAdapter {
        viewHolderItem.adapter = this
        val call = ViewHolderCallBack(viewHolderItem)
        // 一个 for 循环用于遍历全部 item 数量调用 isInHere 回调，添加进 mPositionsWithCallback 数组
        // 向上转型存进数组(能够解决泛型擦除的主要原因并不在这里,主要原因在于接口回调的强转写法)
        mLayoutIdWithCallback[viewHolderItem.layoutId] = call
        return this
    }

    /**
     * 用于设置完所有 Item 后加载 Adapter
     *
     * **WARNING:** 只能在第一次才能使用该方法
     */
    fun show(): SimpleRvAdapter {
        if (allItemCount != 0) {
            throw RuntimeException("SimpleRvAdapter#show(): 该方法只能在一次才能调用")
        }
        mLayoutIdWithCallback.forEach{
            allItemCount += it.value.item.__newMap.size
        }
        notifyItemRangeInserted(0, allItemCount)
        return this
    }

    /**
     * 通过 layoutId 返回是否存在该 item
     */
    fun hasItem(layoutId: Int): Boolean {
        val call = mLayoutIdWithCallback[layoutId]
        return call != null
    }

    /**
     * notifyDataSetChanged() 永远的神(但不建议使用)
     *
     * **NOTE:** 请在你**修改了**所有 Item 的 getItemCount() 后调用
     *
     * **WARNING:** 如果在你数据改变的时候, 不可直接调用 notifyDataSetChanged(), 因为你无法修改 [allItemCount]
     */
    @Deprecated("不建议调用此方法", replaceWith = ReplaceWith("refreshAuto()"))
    fun refreshYYDS() {
        allItemCount = 0
        mLayoutIdWithCallback.forEach{
            allItemCount += it.value.item.__newMap.size
        }
        notifyDataSetChanged()
    }

    /**
     * 单个 item 刷新
     *
     * @param refreshMode 刷新方式
     */
    fun refreshItem(position: Int, refreshMode: Mode = Mode.REFACTOR_MILD) {
        when (refreshMode) {
            Mode.REFACTOR_THROUGH -> notifyItemChanged(position)
            Mode.REFACTOR_MILD, Mode.REFRESH -> notifyItemChanged(position, refreshMode)
        }
    }

    /**
     * 本方法只是内部 Item 调用
     */
    private fun refreshAuto() {
        // 先检查全部的 Item 是否已经准备好了更新
        mLayoutIdWithCallback.forEach { if (!it.value.item.__isPrepareRefresh) { return } }
        DiffUtil.calculateDiff(DiffRefresh()).dispatchUpdatesTo(this)
        mLayoutIdWithCallback.forEach { it.value.item.__refreshOver() }
    }

    private val mLayoutIdWithCallback = HashMap<Int, Callback<*>>() // LayoutId 与 CallBack 的对应关系



    /*
    * =====================================================================================================================
    * 继承来的方法, 里面包含了主要思路
    * */

    override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): RecyclerView.ViewHolder {
        val callBack = mLayoutIdWithCallback.getValue(layoutId)
        val viewHolder = callBack.createNewViewHolder(parent)
        callBack.create(viewHolder) // 在这里用于设置点击监听或其他只用设置一次的东西
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 因为每个 position 对应的 CallBack 不同, 但 CallBack 的数量取决于你的 item 类型
        val call = mLayoutIdWithCallback.getValue(holder.itemViewType)
        call.refactor(holder, position)
    }

    override fun onBindViewHolder( // 如果不知道该方法为什么要重写,请自己百度: 带有三个参数的 onBindViewHolder
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            val call = mLayoutIdWithCallback.getValue(holder.itemViewType)
            payloads.forEach {
                when (it) {
                    Mode.REFACTOR_MILD -> call.refactor(holder, position)
                    Mode.REFRESH -> call.specialRefresh(holder, position)
                }
            }
        }
    }

    private var allItemCount = 0 // 所有 item 数量
    override fun getItemCount(): Int {
        /*
        * 如果你发现这里有问题, 可能是你直接调用了 notifyDataSetChanged(),
        * 请使用 Item 内部整合的 refreshAllItemMap() 代替
        * */
        return allItemCount
    }

    /**
     * 找得到就返回 layoutId, 找不到时就报错
     *
     * @throws RuntimeException 找不到该位置的 item
     */
    override fun getItemViewType(position: Int): Int {
        mLayoutIdWithCallback.forEach {
            if (it.value.item.__newMap.containsKey(position)) {
                return it.value.item.layoutId
            }
        }
        throw RuntimeException("SimpleRVAdapter: 找不到 $position 位置的 Item, " +
                "请检查 Item 中的 isInHere() 方法中是否存在 $position 位置没有设置!")
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val call = mLayoutIdWithCallback[holder.itemViewType]
        call?.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val call = mLayoutIdWithCallback[holder.itemViewType]
        call?.onViewDetachedFromWindow(holder)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        val call = mLayoutIdWithCallback[holder.itemViewType]
        call?.onViewRecycled(holder)
    }

    /**
     * 网上的博客没有讲解过该方法, 只是在对于 RecyclerView 出现闪动时都推荐使用该方法, 但都没讲为什么
     * ```
     * 对于 RecyclerView 出现闪动时网上的解法是:
     * 1、设置该方法为 true
     * 2、重写 getItemId() 直接 return position
     * ```
     * 这种解法确实可以使调用 notifyDataSetChanged() 后不再出现闪动, 但你知道他的原理吗?
     *
     * 其实就是在刷新的时候通过 getItemId() 在缓存中查找是否拥有相同标识的 Item, 有的话就直接拿来用,
     * 而不回调 onBindViewHolder(), 所以你没设置好的话, 就会与其中我整合的差分刷新出现问题, 尤其是在中间位置有数据被删除时
     */
    @Deprecated("已整合了差分刷新, 不建议再使用该方法", ReplaceWith(""))
    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(false)
    }

    /**
     * 原因可以看 [setHasStableIds]
     */
    @Deprecated("已整合了差分刷新, 不建议再使用该方法", ReplaceWith(""))
    override fun getItemId(position: Int): Long {
        return NO_ID
    }

    class BindingVH(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)



    /*
    * ===============================================================================================================
    * 差分刷新的实现类
    * */

    private inner class DiffRefresh: DiffUtil.Callback() {
        val oldItemCount = allItemCount
        var newItemCount = 0
        init {
            mLayoutIdWithCallback.forEach { newItemCount += it.value.item.__newMap.size }
            allItemCount = newItemCount // 更新全部数量
        }
        override fun getOldListSize(): Int = oldItemCount
        override fun getNewListSize(): Int = newItemCount

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            mLayoutIdWithCallback.forEach {
                val item = it.value.item
                val old = item.__oldMap.containsKey(oldItemPosition)
                val new = item.__newMap.containsKey(newItemPosition)
                // 这个说明两个 layoutId 相同, 就是指同一种类型的 ViewHolder, 再由你自己判断张三的位置在哪里
                if (old && new) return item.__compareName(oldItemPosition, newItemPosition)
                else if (old != new) return false
            }
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            mLayoutIdWithCallback.forEach {
                val item = it.value.item
                if (item.__newMap.containsKey(newItemPosition)) {
                    return item.__compareData(oldItemPosition, newItemPosition)
                }
            }
            return false
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            mLayoutIdWithCallback.forEach {
                val item = it.value.item
                if (item.__newMap.containsKey(newItemPosition)) {
                    return when (item.__refreshMode) {
                        Mode.REFACTOR_THROUGH -> null
                        Mode.REFACTOR_MILD, Mode.REFRESH -> item.__refreshMode
                    }
                }
            }
            return null
        }
    }



    /*
    * ==============================================================================================================
    * Callback 类用于管理要用的方法(利用多态的思想), Callback 两个子类(BindingCallBack、ViewHolderCallBack)用于管理泛型
    * */
    private abstract class Callback<T>(
        val item: Item<T>
    ) {
        abstract fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        abstract fun create(holder: RecyclerView.ViewHolder)
        abstract fun refactor(holder: RecyclerView.ViewHolder, position: Int)
        abstract fun specialRefresh(holder: RecyclerView.ViewHolder, position: Int)
        abstract fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder)
        abstract fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
        abstract fun onViewRecycled(holder: RecyclerView.ViewHolder)
    }

    /**
     * 该类使用了装饰器模式: 将原始对象作为一个参数传入给装饰者的构造器
     */
    private class BindingCallBack<DB: ViewDataBinding, T>(
        private val DBItem: DBItem<DB, T>
    ) : Callback<T>(DBItem) {
        override fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return BindingVH(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    DBItem.layoutId, parent, false
                )
            )
        }

        override fun create(holder: RecyclerView.ViewHolder) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onCreate(binding, holder, DBItem.__newMap)
        }

        override fun refactor(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onRefactor(binding, holder, position, DBItem.__newMap.getValue(position))
            binding.executePendingBindings() // 必须调用, 原因: https://stackoom.com/question/3yD45
        }

        override fun specialRefresh(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onSpecialRefresh(binding, holder, position, DBItem.__newMap.getValue(position))
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onViewAttachedToWindow(binding, holder)
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onViewDetachedFromWindow(binding, holder)
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.onViewRecycled(binding, holder)
        }
    }

    /**
     * 该类使用了装饰器模式: 将原始对象作为一个参数传入给装饰者的构造器
     */
    private class ViewHolderCallBack<VH: RecyclerView.ViewHolder, T>(
        private val VHItem: VHItem<VH, T>
    ) : Callback<T>(VHItem) {
        override fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val rootView =
                LayoutInflater.from(parent.context)
                    .inflate(VHItem.layoutId, parent, false)
            return VHItem.getNewViewHolder(rootView)
        }

        override fun create(holder: RecyclerView.ViewHolder) {
            VHItem.onCreate(holder as VH, VHItem.__newMap)
        }

        override fun refactor(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            VHItem.onRefactor(holder as VH, position, VHItem.__newMap.getValue(position))
        }

        override fun specialRefresh(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            VHItem.onSpecialRefresh(holder as VH, position, VHItem.__newMap.getValue(position))
        }

        override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
            VHItem.onViewAttachedToWindow(holder as VH)
        }

        override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
            VHItem.onViewDetachedFromWindow(holder as VH)
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            VHItem.onViewRecycled(holder as VH)
        }
    }


    /*
    * ===============================================================================================================
    * 用于管理你的 Item, 就相当于一种 ViewHolder, 只是我将他进行了分离
    * Item 中包含一个使用差分刷新的方法 refreshAllItemMap()
    * */

    abstract class Item<T>(
        /** 内部变量, **禁止使用** */
        var __newMap: Map<Int, T>,
        val layoutId: Int
    ) {
        constructor(
            list: List<T>,
            startPosition: Int,
            layoutId: Int
        ) : this(
            listToMap(list, startPosition),
            layoutId
        )
        companion object {
            private fun <T> listToMap(list: List<T>, startPosition: Int): Map<Int, T> {
                val map = HashMap<Int, T>(list.size)
                for (i in list.indices) { map[i + startPosition] = list[i] }
                return map
            }
        }

        lateinit var adapter: SimpleRvAdapter
        /** 内部变量, **禁止使用** */
        internal var __isPrepareRefresh = false
        /** 内部变量, **禁止使用** */
        internal var __refreshMode = Mode.REFACTOR_MILD
        /** 内部变量, **禁止使用** */
        internal var __oldMap = HashMap<Int, T>(__newMap)
        /** 内部变量, **禁止使用**, 注释请看 [diffRefreshAllItemMap] */
        internal var __isSameName: (oldData: T, newData: T) -> Boolean = { _, _ ->  false }
        /** 内部变量, **禁止使用**, 注释请看 [diffRefreshAllItemMap] */
        internal var __isSameData: (oldData: T, newData: T) -> Boolean = { _, _ ->  false }

        /** 内部方法, **禁止调用**, 注释请看 [diffRefreshAllItemMap] */
        fun __compareName(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return __isSameName(__oldMap.getValue(oldItemPosition), __newMap.getValue(newItemPosition))
        }

        /** 内部方法, **禁止调用**, 注释请看 [diffRefreshAllItemMap] */
        fun __compareData(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return __isSameData(__oldMap.getValue(oldItemPosition), __newMap.getValue(newItemPosition))
        }

        /** 内部方法, **禁止调用** */
        fun __refreshOver() {
            if (__isPrepareRefresh) {
                __isPrepareRefresh = false
                __refreshMode = Mode.REFACTOR_MILD
                __oldMap.clear()
                __oldMap.putAll(__newMap)
            }
        }

        /**
         * idSameName 是比对两个数据的唯一 id, 是指该数据是否是自己
         * isSameData 比对其他数据是否相同
         * 比如:
         * ```
         *          新数据      旧数据
         * 名字:     张三        张三
         * 学号:     12345      12345
         * 场地:     食堂        寝室
         * 安排:     上课        洗澡
         * 其他：     111        222
         *
         * 如上所示:
         * 1、对于 idSameName 就应该返回新旧数据的 “名字” 或者 “学号” 是否相同, 因为这是两个数据之间的唯一 id(可能名字会有重复)
         * 2、对于 isSameData 就应该返回新旧数据的 “场地” 和  “安排” 和  “其他”  是否 都 相同
         * 2、如果对于某些数据没有名字或学号这种东西, 也可以以那些不会发生改变的数据来作为唯一 id
         * ```
         *
         * @param isSameName 比对两个数据的唯一 id 是否相同
         * @param isSameData 比对其他数据是否相同
         */
        fun diffRefreshAllItemMap(
            map: Map<Int, T>,
            isSameName: (oldData: T, newData: T) -> Boolean,
            isSameData: (oldData: T, newData: T) -> Boolean,
            refreshMode: Mode = Mode.REFACTOR_MILD
        ) {
            __newMap = map
            __refreshMode = refreshMode
            __isPrepareRefresh = true
            __isSameName = isSameName
            __isSameData = isSameData
            adapter.refreshAuto()
        }
        /** 用于传入数据为 list **/
        fun diffRefreshAllItemMap(
            list: List<T>,
            startPosition: Int,
            isSameName: (oldData: T, newData: T) -> Boolean,
            isSameData: (oldData: T, newData: T) -> Boolean,
            refreshMode: Mode = Mode.REFACTOR_MILD
        ) {
            diffRefreshAllItemMap(listToMap(list, startPosition), isSameName, isSameData, refreshMode)
        }

        /**
         * 只用于**没有增加或删除**时刷新自己
         *
         * @param refreshMode 刷新方式
         */
        fun refreshSelfMap(map: Map<Int, T>, refreshMode: Mode = Mode.REFACTOR_MILD) {
            if (map.size != this.__oldMap.size) { return }
            map.forEach { if (!this.__oldMap.containsKey(it.key)) { return } }
            __newMap = map
            __oldMap.clear()
            __oldMap.putAll(map)
            map.forEach {
                adapter.refreshItem(it.key, refreshMode)
            }
        }
        /** 用于传入数据为 list **/
        fun refreshSelfMap(
            list: List<T>,
            startPosition: Int,
            refreshMode: Mode = Mode.REFACTOR_MILD
        ) { refreshSelfMap(listToMap(list, startPosition), refreshMode) }
    }

    /**
     * 用于添加 DataBinding 的 item
     * @param map position 与 T 的键值对关系, 其中 position 是在布局中的位置
     *
     * **WARNING:**
     * ```
     * 注意这个 map 不建议你直接拿来用, 因为在回调中会直接返回 map 中的值.
     * (不让直接用的原因在与内部的差分刷新会使用到这个 map)
     * ```
     */
    abstract class DBItem<DB: ViewDataBinding, T> : Item<T> {
        constructor(map: Map<Int, T>, @LayoutRes layoutId: Int) : super(map, layoutId)
        constructor(list: List<T>, startPosition: Int, @LayoutRes layoutId: Int) : super(list, startPosition, layoutId)
        /**
         * 在 item 创建时的回调, 建议在此处进行一些只需进行一次的操作, 如: 设置点击监听、设置用于 item 整个生命周期的对象
         *
         * **WARNING:** ***禁止在这里使用 kotlin 的扩展插件只使用 layoutId 得到 View***
         *
         * **WARNING:** 在该方法中并**不能直接**得到当前 item 的 ***position***, 但对于设置**点击事件等回调除外**,
         * 可以使用 ***holder.adapterPosition*** 或者 ***holder.layoutPosition*** 得到
         * ```
         * (简单插一句, 对于 holder.adapterPosition 与 holder.layoutPosition 的区别
         * 可以查看: https://blog.csdn.net/u013467495/article/details/109078905?utm_
         * medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogComme
         * ndFromBaidu%7Edefault-10.pc_relevant_baidujshouduan&depth_1-utm_sour
         * ce=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFr
         * omBaidu%7Edefault-10.pc_relevant_baidujshouduan)
         * ```
         */
        abstract fun onCreate(binding: DB, holder: BindingVH, map: Map<Int, T>)

        /**
         * 用于设置当前 item **每次进入屏幕**显示的数据(包括离开屏幕又回到屏幕)
         *
         * **WARNING:** ***禁止在这里使用 kotlin 的扩展插件只使用 layoutId 得到 View***
         *
         * **WARNING:** **点击事件等回调不能写在这里**,
         * ```
         * 原因在于: https://blog.csdn.net/weixin_28318011/article/details/112872952
         * ```
         * **NOTE:** 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
         *
         * **WARNING:** **->> 请不要在此处创建任何新的对象 <<-**
         * ```
         * 比如：设置点击监听(会生成匿名内部类)、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法,
         * ```
         * ***->> 这些做法应写在 [onCreate] 中 <<-***
         *
         * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
         */
        abstract fun onRefactor(binding: DB, holder: BindingVH, position: Int, value: T)

        /**
         * 特殊刷新, 使用 [Mode.REFRESH] 后刷新当前 item 的回调
         *
         * **NOTE:** 它的修改周期只会在屏幕内, 离开后可能就会还原.
         * ```
         * 因为离开后再回来就只会回调 refactor(), 解决办法是数据修改后就更改全局数组, 在 refactor() 中直接取数组中的值
         * ```
         */
        open fun onSpecialRefresh(binding: DB, holder: BindingVH, position: Int, value: T) {}

        /**
         * 当这个 holder 显示在屏幕上时
         */
        open fun onViewAttachedToWindow(binding: DB, holder: BindingVH) {}

        /**
         * 当这个 holder 从屏幕离开时
         */
        open fun onViewDetachedFromWindow(binding: DB, holder: BindingVH) {}

        /**
         * 当这个 holder 被回收时(在调用刷新传入 isRefactor 为 true 后 item 会被回收, 此时就会回调该方法)
         */
        open fun onViewRecycled(binding: DB, holder: BindingVH) {}
    }

    /**
     * 用于添加 ViewHolder 的 item
     * @param map position 与 T 的键值对关系, 其中 position 是在布局中的位置
     *
     * **WARNING:**
     * ```
     * 注意这个 map 不建议你直接拿来用, 因为在回调中会直接返回 map 中的值.
     * (不让直接用的原因在与内部的差分刷新会使用到这个 map)
     * ```
     */
    abstract class VHItem<VH: RecyclerView.ViewHolder, T> : Item<T> {
        constructor(map: Map<Int, T>, @LayoutRes layoutId: Int) : super(map, layoutId)
        constructor(list: List<T>, startPosition: Int, @LayoutRes layoutId: Int) : super(list, startPosition, layoutId)
        /**
         * 返回一个新的 ViewHolder，**请不要返回相同的对象**
         */
        abstract fun getNewViewHolder(itemView: View): VH

        /**
         * 在 item 创建时的回调, 建议在此处进行一些只需进行一次的操作, 如: 设置点击监听、设置用于 item 整个生命周期的对象
         *
         * **WARNING:** ***禁止在这里使用 kotlin 的扩展插件只使用 layoutId 得到 View***
         *
         * **WARNING:** 在该方法中并**不能直接**得到当前 item 的 ***position***, 但对于设置**点击事件等回调除外**,
         * 可以使用 ***holder.adapterPosition*** 或者 ***holder.layoutPosition*** 得到
         * ```
         * (简单插一句, 对于 holder.adapterPosition 与 holder.layoutPosition 的区别
         * 可以查看: https://blog.csdn.net/u013467495/article/details/109078905?utm_
         * medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogComme
         * ndFromBaidu%7Edefault-10.pc_relevant_baidujshouduan&depth_1-utm_sour
         * ce=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFr
         * omBaidu%7Edefault-10.pc_relevant_baidujshouduan)
         * ```
         */
        abstract fun onCreate(holder: VH, map: Map<Int, T>)

        /**
         * 用于设置当前 item **每次进入屏幕**显示的数据(包括离开屏幕又回到屏幕)
         *
         * **WARNING:** ***禁止在这里使用 kotlin 的扩展插件只使用 layoutId 得到 View***
         *
         * **WARNING:** **点击事件等回调不能写在这里**,
         * ```
         * 原因在于: https://blog.csdn.net/weixin_28318011/article/details/112872952
         * ```
         * **NOTE:** 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
         *
         * **WARNING:** **->> 请不要在此处创建任何新的对象 <<-**
         * ```
         * 比如：设置点击监听(会生成匿名内部类)、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法,
         * ```
         * ***->> 这些做法应写在 [onCreate] 中 <<-***
         *
         * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
         */
        abstract fun onRefactor(holder: VH, position: Int, value: T)

        /**
         * 特殊刷新, 使用 [Mode.REFRESH] 后刷新当前 item 的回调
         *
         * **NOTE:** 它的修改周期只会在屏幕内, 离开后可能就会还原.
         * ```
         * 因为离开后再回来就只会回调 onRefactor(), 解决办法是数据修改后就更改全局数组, 在 onRefactor() 中直接取数组中的值
         * ```
         */
        open fun onSpecialRefresh(holder: VH, position: Int, value: T) {}

        /**
         * 当这个 holder 显示在屏幕上时
         */
        open fun onViewAttachedToWindow(holder: VH) {}

        /**
         * 当这个 holder 从屏幕离开时
         */
        open fun onViewDetachedFromWindow(holder: VH) {}

        /**
         * 当这个 holder 被回收时(在调用刷新传入 isRefactor 为 true 后 item 会被回收, 此时就会回调该方法)
         */
        open fun onViewRecycled(holder: VH) {}
    }



    /*
    * ===============================================================================================================
    * 刷新方式的选择, 想了解原理的话可以网上搜索 RecyclerView 带有三个参数的 onBindViewHolder 方法
    * */

    enum class Mode {
        /**
         * 彻底的刷新
         * ```
         * 该刷新会使当前 item 更换为缓存中的其他 item, 会回调 onRefactor() 方法刷新
         * ```
         * 缺点:
         * ```
         * 1、最费内存和时间的刷新, 对于在 Rv 中展示图片时, 会出现图片闪动的问题
         * [此刷新根据 Rv 的刷新机制, 会换掉整个 item (从缓存里面找到相同的 item 来替换),
         *  此时如果有图片, 可能会出现图片闪动的问题, 建议在没有图片, 只有一些文字修改时使用该方式.]
         * ```
         * 优点:
         * ```
         * 1、最彻底的刷新, 全部数据都进行重新绘制(因为换了一个相同的 item 重新设置数据)
         * ```
         */
        REFACTOR_THROUGH,

        /**
         * 温和的刷新(确实想不到取什么名字好)
         * ```
         * 该刷新不会将当前 item 与缓存中的进行更换, 会回调 onRefactor() 方法刷新
         * ```
         * 缺点:
         * ```
         * 1、好像没有缺点, 绝大部分需求用它就可以解决
         * ```
         * 优点:
         * ```
         * 1、内存和时间消耗最低, 相当于对目前展示的 item 直接修改数据
         * 2、可以解决 Rv 中展示图片时出现的图片闪动问题
         * ```
         */
        REFACTOR_MILD,

        /**
         * 与 [REFACTOR_MILD] 相同, 但是回调是 Item 的 onRefresh(), 不是 onRefactor()
         * ```
         * 该刷新可以得到里面的显示的 View, 然后回调 Item 的 onRefresh(), 可以在这里进行一些特殊操作
         * ```
         */
        REFRESH
    }
}