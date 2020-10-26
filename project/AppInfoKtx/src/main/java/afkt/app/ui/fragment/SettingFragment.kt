package afkt.app.ui.fragment

import afkt.app.R
import afkt.app.base.BaseFragment
import afkt.app.base.Constants
import afkt.app.databinding.FragmentSettingBinding
import afkt.app.module.event.SortEvent
import afkt.app.ui.dialog.AppSortDialog
import afkt.app.ui.dialog.QuerySuffixDialog
import afkt.app.utils.AppListUtils
import afkt.app.utils.ProjectUtils
import afkt.app.utils.QuerySuffixUtils
import android.os.Bundle
import android.view.View
import dev.utils.app.ResourceUtils
import dev.utils.app.share.SharedUtils
import dev.utils.app.toast.ToastTintUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    // = Object =

    private var appSortArys: Array<String>? = null // 排序数据

    override fun baseContentId(): Int {
        return R.layout.fragment_setting
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vidFsAppsortLinear.setOnClickListener { AppSortDialog(context).show() }
        binding.vidFsScanapkLinear.setOnClickListener { QuerySuffixDialog(context).show() }
        binding.vidFsResetLinear.setOnClickListener {
            SharedUtils.put(Constants.Key.KEY_APP_SORT, 0)
            QuerySuffixUtils.reset() // 清空后缀
            AppListUtils.reset() // 清空应用列表
            selectAppSort() // 重置排序文案
            ToastTintUtils.success(ResourceUtils.getString(R.string.str_reset_desetting_suc))
        }
        appSortArys = ResourceUtils.getStringArray(R.array.array_app_sort)
        selectAppSort()
    }

    /**
     * 设置选择排序文案
     */
    private fun selectAppSort() {
        binding.vidFsAppsortTv.setText(appSortArys!![ProjectUtils.getAppSortType()])
    }

    // ===========
    // = 事件相关 =
    // ===========

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SortEvent) {
        selectAppSort()
    }
}