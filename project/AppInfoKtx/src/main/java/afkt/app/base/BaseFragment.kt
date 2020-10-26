package afkt.app.base

import afkt.app.utils.EventBusUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import dev.base.expand.viewbinding.DevBaseViewBindingFragment

abstract class BaseFragment<VB : ViewBinding> : DevBaseViewBindingFragment<VB>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        readArguments()
        if (isRegister()) EventBusUtils.register(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegister()) EventBusUtils.unregister(this)
    }

    // 是否注册 EventBus
    open fun isRegister(): Boolean {
        return true
    }

    // 读取传参数据
    open fun readArguments() {
    }

    // ===================
    // = IDevBaseContent =
    // ===================

    override fun baseContentView(): View? {
        return null
    }

    // =======================
    // = IDevBaseViewBinding =
    // =======================

    override fun getBindingView(): View? {
        return mContentView
    }
}