package afkt.accessibility.ui.widget

import afkt.accessibility.R
import afkt.accessibility.module.event.ActivityChangedEvent
import afkt.accessibility.service.AccessibilityListenerService
import afkt.accessibility.utils.EventBusUtils
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import dev.utils.app.helper.ViewHelper
import dev.utils.app.logger.DevLogger
import dev.utils.app.toast.ToastUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * detail: 浮动 View
 * @author jinliangshan
 */
class FloatingView : LinearLayout {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private val TAG = FloatingView::class.java.simpleName

    private var mWindowManager: WindowManager? = null
    private var packageNameTv: TextView? = null
    private var classNameTv: TextView? = null

    private fun init() {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        View.inflate(context, R.layout.layout_floating, this)
        packageNameTv = findViewById(R.id.vid_lf_package_name_tv)
        classNameTv = findViewById(R.id.vid_lf_class_name_tv)
        findViewById<View>(R.id.vid_lf_icon_igview).setOnClickListener(OnClickListener {
            ToastUtils.showShort(R.string.str_close_floating)

            context.startService(
                Intent(context, AccessibilityListenerService::class.java)
                    .putExtra(
                        AccessibilityListenerService.COMMAND,
                        AccessibilityListenerService.COMMAND_CLOSE
                    )
            )
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventBusUtils.register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBusUtils.unregister(this)
    }

    private var preP: Point = Point()
    private var curP: Point = Point()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> preP = Point(event.rawX.toInt(), event.rawY.toInt())
            MotionEvent.ACTION_MOVE -> {
                curP = Point(event.rawX.toInt(), event.rawY.toInt())
                val dx: Int = curP.x - preP.x
                val dy: Int = curP.y - preP.y
                val layoutParams = this.layoutParams as WindowManager.LayoutParams
                layoutParams.x += dx
                layoutParams.y += dy
                mWindowManager?.updateViewLayout(this, layoutParams)
                preP = curP
            }
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ActivityChangedEvent?) {
        event?.let {
            val packageName = it.packageName
            val className = it.className

            val trimName =
                if (className.startsWith(packageName)) className.substring(packageName.length) else className

            DevLogger.dTag(TAG, "%s -> %s", packageName, trimName)

            ViewHelper.get()
                .setText(packageNameTv, packageName)
                .setText(classNameTv, trimName)
        }
    }
}