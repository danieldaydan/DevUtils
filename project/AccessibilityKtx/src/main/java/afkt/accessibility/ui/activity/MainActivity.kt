package afkt.accessibility.ui.activity

import afkt.accessibility.R
import afkt.accessibility.databinding.ActivityMainBinding
import afkt.accessibility.service.AccessibilityListenerService
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import dev.utils.app.ActivityUtils
import dev.utils.app.IntentUtils
import dev.utils.app.toast.ToastUtils

/**
 * <pre>
 *     具体代码 copy Github ActivityTracker 库
 *     @see <a href="https://github.com/fashare2015/ActivityTracker"/>
 *     <p></p>
 *     内部只是封装 AccessibilityListenerService 以及 BaseApplication 结合 DevApp#AccessibilityUtils 使用
 *     可自行实现其他功能模块, 例如微信抢红包、自动点击等操作
 * </pre>
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // = Object =

    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkOverlayPermission()

        // Activity 栈
        binding.vidAmActivityTrackerBtn.setOnClickListener {
            if (AccessibilityListenerService.checkAccessibility()) {
                startService(
                    Intent(this@MainActivity, AccessibilityListenerService::class.java)
                        .putExtra(
                            AccessibilityListenerService.COMMAND,
                            AccessibilityListenerService.COMMAND_OPEN
                        )
                )
                ActivityUtils.startHomeActivity()
            } else {
                ToastUtils.showShort(R.string.str_open_accessibility_tips)
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && !Settings.canDrawOverlays(this)) {
            checkOverlayPermission()
        }
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(
                    IntentUtils.getManageOverlayPermissionIntent(),
                    REQUEST_CODE
                )
                ToastUtils.showShort(R.string.str_open_floating_tips)
            }
        }
    }
}