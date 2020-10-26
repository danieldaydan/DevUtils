package afkt.app.utils

import afkt.app.R
import afkt.app.base.config.PathConfig
import android.content.Intent
import android.net.Uri
import dev.utils.app.AppUtils
import dev.utils.app.HandlerUtils
import dev.utils.app.ResourceUtils
import dev.utils.app.info.ApkInfoItem
import dev.utils.app.info.AppInfoBean
import dev.utils.app.info.AppInfoItem
import dev.utils.app.toast.ToastTintUtils
import dev.utils.common.FileUtils
import dev.utils.common.thread.DevThreadManager
import java.io.File

/**
 * detail: 导出工具类 ( 合并减少代码 )
 * @author Ttt
 */
object ExportUtils {

    interface Callback {
        fun onExport(file: File)
    }

    // ===========
    // = 导出信息 =
    // ===========

    /**
     * 导出 APP 信息
     */
    fun exportInfo(appInfoItem: AppInfoItem) {
        exportInfo(true, appInfoItem.appInfoBean, ProjectUtils.toJson(appInfoItem))
    }

    /**
     * 导出 APK 信息
     */
    fun exportInfo(apkInfoItem: ApkInfoItem) {
        exportInfo(false, apkInfoItem.appInfoBean, ProjectUtils.toJson(apkInfoItem))
    }

    /**
     * 导出信息
     * @param isApp 是否 app
     * @param appInfoBean App 信息
     * @param info 导出信息
     */
    private fun exportInfo(isApp: Boolean, appInfoBean: AppInfoBean, info: String?) {
        HandlerUtils.postRunnable {
            val path = if (isApp) PathConfig.AEP_APPMSG_PATH else PathConfig.AEP_APKMSG_PATH
            // 应用名_包名_版本名.txt
            var fileName =
                appInfoBean.appName + "_" + appInfoBean.appPackName + "_" + appInfoBean.versionName + ".txt"
            // 导出数据
            var result = FileUtils.saveFile(
                FileUtils.getFile(path, fileName),
                info?.toByteArray()
            )
            if (result) {
                ToastTintUtils.success(
                    ResourceUtils.getString(R.string.str_export_suc)
                            + " " + path + fileName
                )
            } else {
                ToastTintUtils.error(ResourceUtils.getString(R.string.str_export_fail))
            }
        }
    }

    // ===========
    // = 导出应用 =
    // ===========

    /**
     * 导出 APP
     */
    fun exportApp(appInfoItem: AppInfoItem) {
        exportApp(appInfoItem.appInfoBean, null)
    }

    /**
     * 导出 APK
     */
    fun exportApp(apkInfoItem: ApkInfoItem) {
        exportApp(apkInfoItem.appInfoBean, null)
    }

    /**
     * 导出信息
     * @param isApp 是否 app
     * @param appInfoBean App 信息
     * @param info 导出信息
     */
    private fun exportApp(appInfoBean: AppInfoBean, callback: Callback?) {
        DevThreadManager.getInstance(3).execute {
            // 应用名_包名_版本名.apk
            var fileName =
                appInfoBean.appName + "_" + appInfoBean.appPackName + "_" + appInfoBean.versionName + ".apk"

            val destFile = FileUtils.getFile(PathConfig.AEP_APK_PATH, fileName)
            val destPath = destFile.absolutePath
            if (destFile.exists()) {
                ToastTintUtils.success(
                    ResourceUtils.getString(R.string.str_export_suc) + " " + destPath
                )
                callback?.let { HandlerUtils.postRunnable { it.onExport(destFile) } }
                return@execute
            }
            // 提示导出中
            ToastTintUtils.normal(ResourceUtils.getString(R.string.str_export_ing))
            // 导出应用
            var result = FileUtils.copyFile(
                appInfoBean.sourceDir, destPath, true
            )
            if (result) {
                ToastTintUtils.success(
                    ResourceUtils.getString(R.string.str_export_suc) + " " + destPath
                )
                callback?.let { HandlerUtils.postRunnable { it.onExport(destFile) } }
            } else {
                ToastTintUtils.error(ResourceUtils.getString(R.string.str_export_fail))
            }
        }
    }

    // ===========
    // = 分享应用 =
    // ===========

    /**
     * 分享 APP
     */
    fun shareApp(appInfoItem: AppInfoItem) {
        exportApp(appInfoItem.appInfoBean, object : Callback {
            override fun onExport(file: File) {
                shareApp(file)
            }
        })
    }

    /**
     * 分享 APK
     */
    fun shareApp(apkInfoItem: ApkInfoItem) {
        exportApp(apkInfoItem.appInfoBean, object : Callback {
            override fun onExport(file: File) {
                shareApp(file)
            }
        })
    }

    /**
     * 分享
     */
    private fun shareApp(file: File) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            AppUtils.startActivity(intent)
        } catch (e: java.lang.Exception) {
            ToastTintUtils.error(ResourceUtils.getString(R.string.str_share_fail))
        }
    }
}