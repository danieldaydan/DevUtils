package afkt.app.ui.fragment

import afkt.app.R
import afkt.app.base.BaseFragment
import afkt.app.base.Constants
import afkt.app.base.config.PathConfig
import afkt.app.databinding.FragmentInfoBinding
import afkt.app.module.TypeEnum
import afkt.app.module.bean.DeviceInfoBean
import afkt.app.module.event.ExportEvent
import afkt.app.module.event.FragmentEvent
import afkt.app.module.event.InfoEvent
import afkt.app.ui.adapter.InfoAdapter
import afkt.app.utils.EventBusUtils
import afkt.app.utils.ProjectUtils
import android.os.Bundle
import android.view.View
import dev.utils.app.ResourceUtils
import dev.utils.app.toast.ToastTintUtils
import dev.utils.common.FileUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InfoFragment : BaseFragment<FragmentInfoBinding>() {

    companion object {
        fun get(type: TypeEnum): BaseFragment<FragmentInfoBinding> {
            val fragment = InfoFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.Key.KEY_VALUE, type.tag)
            fragment.setArguments(bundle)
            return fragment
        }
    }

    // = Object =

    private var type: TypeEnum? = null

    override fun baseContentId(): Int {
        return R.layout.fragment_info
    }

    override fun readArguments() {
        super.readArguments()

        var value = arguments?.getInt(Constants.Key.KEY_VALUE)
        type = value?.let { TypeEnum.get(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setEnableRefresh(false)
            .setEnableLoadMore(false)
    }

    // ===========
    // = 事件相关 =
    // ===========

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun onEvent(event: FragmentEvent) {
        event.type?.let {
            if (it == type && binding.root.getAdapter<InfoAdapter>() == null) {
                when (it) {
                    TypeEnum.DEVICE_INFO -> ProjectUtils.getDeviceInfos()
                    TypeEnum.SCREEN_INFO -> ProjectUtils.getScreenInfos()
                }
                EventBusUtils.removeStickyEvent(FragmentEvent::class.java)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: InfoEvent) {
        event.type?.let {
            if (it == type) binding.root.setAdapter(InfoAdapter(event.lists))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ExportEvent) {
        event.type?.let {
            if (it == type) {
                if (binding.root.getAdapter<InfoAdapter>() != null) {
                    var adapter: InfoAdapter? = binding.root.getAdapter()
                    if (adapter?.data != null) {
                        val content: String? = DeviceInfoBean.jsonString(adapter?.data)
                        var fileName =
                            if (TypeEnum.DEVICE_INFO == it) "device_info.txt" else "screen_info.txt"
                        // 导出数据
                        var result = FileUtils.saveFile(
                            FileUtils.getFile(PathConfig.AEP_PATH, fileName),
                            content?.toByteArray()
                        )
                        if (result) {
                            ToastTintUtils.success(ResourceUtils.getString(R.string.str_export_suc))
                            return
                        }
                    }
                    ToastTintUtils.error(ResourceUtils.getString(R.string.str_export_fail))
                }
            }
        }
    }
}