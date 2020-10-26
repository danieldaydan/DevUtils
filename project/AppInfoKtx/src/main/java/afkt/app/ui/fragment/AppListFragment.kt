package afkt.app.ui.fragment

import afkt.app.R
import afkt.app.base.BaseFragment
import afkt.app.base.Constants
import afkt.app.databinding.FragmentAppBinding
import afkt.app.module.ActionEnum
import afkt.app.module.TypeEnum
import afkt.app.module.event.*
import afkt.app.ui.adapter.AppListAdapter
import afkt.app.utils.AppListUtils
import afkt.app.utils.AppSearchUtils
import afkt.app.utils.EventBusUtils
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.tt.whorlviewlibrary.WhorlView
import dev.utils.app.ListViewUtils
import dev.utils.app.ResourceUtils
import dev.utils.app.TextViewUtils
import dev.utils.app.ViewUtils
import dev.utils.common.HtmlUtils
import dev.widget.assist.ViewAssist
import dev.widget.function.StateLayout
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AppListFragment : BaseFragment<FragmentAppBinding>() {

    companion object {
        fun get(type: TypeEnum): BaseFragment<FragmentAppBinding> {
            val fragment = AppListFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.Key.KEY_VALUE, type.tag)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    // = View =

    private var whorlView: WhorlView? = null

    // = Object =

    private var type: TypeEnum? = null
    private var searchContent: String = ""

    override fun baseContentId(): Int {
        return R.layout.fragment_app
    }

    override fun readArguments() {
        super.readArguments()

        var value = arguments?.getInt(Constants.Key.KEY_VALUE)
        type = value?.let { TypeEnum.get(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vidFaRefresh.setEnableLoadMore(false)

        whorlView = ViewUtils.findViewById(
            binding.vidFaState.getView(ViewAssist.TYPE_ING),
            R.id.vid_sli_load_view
        )
        // 设置监听
        binding.vidFaState.setListener(object : StateLayout.Listener {
            override fun onRemove(layout: StateLayout, type: Int, removeView: Boolean) {
            }

            override fun onNotFound(layout: StateLayout, type: Int) {
                if (type == ViewAssist.TYPE_SUCCESS) {
                    ViewUtils.reverseVisibilitys(true, binding.vidFaRefresh, binding.vidFaState)
                    whorlView?.stop()
                    binding.vidFaRefresh.finishRefresh()
                }
            }

            override fun onChange(layout: StateLayout, type: Int, oldType: Int, view: View) {
                if (ViewUtils.reverseVisibilitys(
                        type == ViewAssist.TYPE_SUCCESS,
                        binding.vidFaRefresh, binding.vidFaState
                    )
                ) {
                    whorlView?.stop()
                    binding.vidFaRefresh.finishRefresh()
                } else {
                    if (type == ViewAssist.TYPE_ING) {
                        if (whorlView != null && !whorlView!!.isCircling()) {
                            whorlView?.start()
                        }
                    } else {
                        whorlView?.stop()
                        // 无数据处理
                        if (type == ViewAssist.TYPE_EMPTY_DATA) {
                            binding.vidFaRefresh.finishRefresh()
                            var tips = if (searchContent.isEmpty()) {
                                ResourceUtils.getString(R.string.str_search_noresult_tips_1)
                            } else {
                                ResourceUtils.getString(
                                    R.string.str_search_noresult_tips,
                                    HtmlUtils.addHtmlColor(searchContent, "#359AFF")
                                )
                            }
                            TextViewUtils.setHtmlText(
                                view.findViewById<TextView>(R.id.vid_slnd_tips_tv), tips
                            )
                        }
                    }
                }
            }
        })
        binding.vidFaState.showIng()

        // 设置刷新事件
        binding.vidFaRefresh.setOnRefreshListener(OnRefreshListener {
            type?.let { AppListUtils.getAppLists(it, true) }
        })
    }

    // ===========
    // = 事件相关 =
    // ===========

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun onEvent(event: FragmentEvent) {
        event.type?.let {
            if (it == type) {
                searchContent = "" // 切换 Fragment 重置搜索内容
                AppListUtils.getAppLists(it) // 加载列表
                EventBusUtils.removeStickyEvent(FragmentEvent::class.java)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: AppListEvent) {
        event.type?.let {
            if (it == type) {
                var lists = if (searchContent.isEmpty()) {
                    event.lists
                } else {
                    AppSearchUtils.filterAppList(event.lists, searchContent)
                }
                if (lists.isEmpty()) {
                    binding.vidFaState.showEmptyData()
                } else {
                    binding.vidFaRefresh.setAdapter(AppListAdapter(lists))
                    binding.vidFaState.showSuccess()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SearchEvent) {
        event.type?.let {
            when (event.action) {
                ActionEnum.COLLAPSE -> { // 搜索合并
                    if (it == type) {
                        if (searchContent.isNotEmpty()) { // 输入内容才刷新列表
                            searchContent = ""
                            AppListUtils.getAppLists(it) // 加载列表
                        }
                    }
                }
                ActionEnum.EXPAND -> { // 搜索展开
                }
                ActionEnum.CONTENT -> { // 搜索输入内容
                    if (it == type) {
                        searchContent = event.content
                        AppListUtils.getAppLists(it) // 加载列表
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        event.type?.let {
            if (it == type) binding.vidFaRefresh.getRefreshLayout()?.autoRefresh()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: TopEvent) {
        event.type?.let {
            if (it == type) {
                ListViewUtils.smoothScrollToTop(binding.vidFaRefresh.getRecyclerView())
                //ListViewUtils.scrollToTop(binding.vidFaRefresh.getRecyclerView())
            }
        }
    }
}