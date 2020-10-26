package afkt.accessibility.base

import afkt.accessibility.BuildConfig
import afkt.accessibility.module.event.ActivityChangedEvent
import afkt.accessibility.service.AccessibilityListenerService
import afkt.accessibility.service.AccessibilityListenerService.AccessibilityListener
import afkt.accessibility.utils.EventBusUtils
import android.os.Build
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.multidex.MultiDexApplication
import dev.DevUtils
import dev.utils.app.logger.DevLogger
import dev.utils.app.logger.LogConfig
import dev.utils.app.logger.LogLevel
import dev.utils.common.StringUtils

class BaseApplication : MultiDexApplication() {

    private val TAG = "AccessibilityKtx_TAG"

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // 初始化 Logger 配置
            DevLogger.init(
                LogConfig()
                    .logLevel(LogLevel.DEBUG)
                    .tag(TAG)
                    .sortLog(true)
                    .methodCount(0)
            )
            // 打开 lib 内部日志 - 线上环境, 不调用方法
            DevUtils.openLog()
            DevUtils.openDebug()
        }
        // 初始化服务
        initService()
    }

    /**
     * 初始化服务
     */
    private fun initService() {
        AccessibilityListenerService.setAccessibilityListener(object : AccessibilityListener() {
            override fun onAccessibilityEvent(
                event: AccessibilityEvent?,
                service: AccessibilityListenerService
            ) {
                // 打印 Event 信息
//                AccessibilityUtils.printAccessibilityEvent(event)

                event?.let {
                    // 窗口改变时通知
                    if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                        val packageName = it.packageName
                        val className = it.className
                        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className)) {
                            EventBusUtils.post(
                                ActivityChangedEvent(
                                    it.packageName.toString(),
                                    it.className.toString()
                                )
                            )
                        }
                    }
                }

                // ===============
                // = 打印事件信息 =
                // ===============

                val print = false
                if (print) { // 开发时打印, 用于获取事件节点信息
                    val builder = StringBuilder()
                    if (service.rootInActiveWindow != null) {
                        track(service.rootInActiveWindow, builder, 0)
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            val lists = service.windows
                            for (info in lists) {
                                info?.let { track(it.root, builder, 0) }
                            }
                        }
                    }
                    event?.let {
                        track(it.source, builder, 0)
                    }
                    DevLogger.w(builder.toString())
                }
            }
        })
        AccessibilityListenerService.startService()
    }

    /**
     * 追踪节点信息
     */
    private fun track(info: AccessibilityNodeInfo?, builder: StringBuilder, index: Int) {
        info?.let {
            if (it.childCount == 0) {
                builder
                    .append(StringUtils.NEW_LINE_STR)
                    .append(StringUtils.appendSpace(index) + "child widget: " + it.className)
                    .append(StringUtils.NEW_LINE_STR)
                    .append(StringUtils.appendSpace(index) + "showDialog: " + it.canOpenPopup())
                    .append(StringUtils.NEW_LINE_STR)
                    .append(StringUtils.appendSpace(index) + "windowId: " + it.windowId)
                    .append(StringUtils.NEW_LINE_STR)
                    .append(StringUtils.appendSpace(index) + "Text: " + it.text)
                    .append(StringUtils.NEW_LINE_STR)
                    .append(StringUtils.appendSpace(index) + "itViewId: " + it.viewIdResourceName)
            } else {
                for (i in 0 until it.childCount) {
                    track(it.getChild(i), builder, index + 1)
                }
            }
        }
    }
}