package dev.utils.app;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;
import androidx.annotation.StringRes;

import java.util.UUID;

import dev.utils.LogPrintUtils;
import dev.utils.common.StringUtils;

/**
 * detail: APP 通用工具类
 * @author Ttt
 * <pre>
 *     所需权限
 *     <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * </pre>
 */
public final class AppCommonUtils {

    private AppCommonUtils() {
    }

    // 日志 TAG
    private static final String TAG = AppCommonUtils.class.getSimpleName();

    // 应用、设备信息 ( 可用于 FileRecordUtils 插入信息使用 )
    private static String APP_DEVICE_INFO = null;

    /**
     * 获取应用、设备信息
     * @return 应用、设备信息
     */
    public static String getAppDeviceInfo() {
        return APP_DEVICE_INFO;
    }

    /**
     * 刷新应用、设备信息
     * @return 应用、设备信息
     */
    public static String refreshAppDeviceInfo() {
        try {
            StringBuilder builder = new StringBuilder();
            // 获取 APP 版本信息
            String[] versions = ManifestUtils.getAppVersion();
            String versionName = versions[0];
            String versionCode = versions[1];
            String packageName = AppUtils.getPackageName();
            String deviceInfo = DeviceUtils.handlerDeviceInfo(DeviceUtils.getDeviceInfo(), null);
            if (TextUtils.isEmpty(versionName) || TextUtils.isEmpty(versionCode) ||
                    TextUtils.isEmpty(packageName) || TextUtils.isEmpty(deviceInfo)) {
                return null;
            }
            // 保存 APP 版本信息
            builder.append("versionName: " + versionName);
            builder.append(StringUtils.NEW_LINE_STR);
            builder.append("versionCode: " + versionCode);
            builder.append(StringUtils.NEW_LINE_STR);
            builder.append("package: " + packageName);
            builder.append(StringUtils.NEW_LINE_STR);
            builder.append(deviceInfo);
            // 设置应用、设备信息
            APP_DEVICE_INFO = builder.toString();
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "refreshAppDeviceInfo");
        }
        return APP_DEVICE_INFO;
    }

    // =

    /**
     * 获取设备唯一 UUID
     * @return 设备唯一 UUID
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    public static String getUUID() {
        return PhoneUtils.getUUID();
    }

    /**
     * 获取设备唯一 UUID ( 使用硬件信息拼凑出来的 )
     * @return 设备唯一 UUID
     * <pre>
     *     https://developer.android.com/training/articles/user-data-ids
     * </pre>
     */
    public static String getUUIDDevice() {
        String serial = "serial";
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; // 13 位
        // 使用硬件信息拼凑出来的 15 位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    // =

    /**
     * 获取 R.string 资源的格式化字符串
     * @param resId      R.string.id
     * @param formatArgs 格式化参数
     * @return 格式化后的字符串
     */
    public static String getFormatRes(@StringRes final int resId, final Object... formatArgs) {
        return getFormatRes(false, resId, formatArgs);
    }

    /**
     * 获取 R.string 资源的格式化字符串
     * @param errorMsg   是否设置异常信息
     * @param resId      R.string.id
     * @param formatArgs 格式化参数
     * @return 格式化后的字符串
     */
    public static String getFormatRes(final boolean errorMsg, @StringRes final int resId, final Object... formatArgs) {
        try {
            if (formatArgs != null && formatArgs.length != 0) {
                return ResourceUtils.getString(resId, formatArgs);
            } else {
                return ResourceUtils.getString(resId);
            }
        } catch (Exception e) {
            LogPrintUtils.eTag(TAG, e, "getFormatRes");
            if (errorMsg && e != null) {
                return e.getMessage();
            }
        }
        return null;
    }

    // ===============
    // = 版本判断处理 =
    // ===============

    /**
     * 获取 SDK 版本
     * @return SDK 版本
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 是否在 2.2 版本及以上
     * @return 是否在 2.2 版本及以上
     */
    public static boolean isFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * 是否在 2.3 版本及以上
     * @return 是否在 2.3 版本及以上
     */
    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * 是否在 2.3.3 版本及以上
     * @return 是否在 2.3.3 版本及以上
     */
    public static boolean isGingerbreadMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    /**
     * 是否在 3.0 版本及以上
     * @return 是否在 3.0 版本及以上
     */
    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * 是否在 3.1 版本及以上
     * @return 是否在 3.1 版本及以上
     */
    public static boolean isHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * 是否在 4.0 版本及以上
     * @return 是否在 4.0 版本及以上
     */
    public static boolean isIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * 是否在 4.0.3 版本及以上
     * @return 是否在 4.0.3 版本及以上
     */
    public static boolean isIceCreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * 是否在 4.1 版本及以上
     * @return 是否在 4.1 版本及以上
     */
    public static boolean isJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * 是否在 4.2 版本及以上
     * @return 是否在 4.2 版本及以上
     */
    public static boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * 是否在 4.3 版本及以上
     * @return 是否在 4.3 版本及以上
     */
    public static boolean isJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * 是否在 4.4.2 版本及以上
     * @return 是否在 4.4.2 版本及以上
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 是否在 5.0.1 版本及以上
     * @return 是否在 5.0.1 版本及以上
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否在 6.0 版本及以上
     * @return 是否在 6.0 版本及以上
     */
    public static boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否在 7.0 版本及以上
     * @return 是否在 7.0 版本及以上
     */
    public static boolean isN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 是否在 7.1.1 版本及以上
     * @return 是否在 7.1.1 版本及以上
     */
    public static boolean isN_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    }

    /**
     * 是否在 8.0 版本及以上
     * @return 是否在 8.0 版本及以上
     */
    public static boolean isO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * 是否在 8.1 版本及以上
     * @return 是否在 8.1 版本及以上
     */
    public static boolean isO_MR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1;
    }

    /**
     * 是否在 9.0 版本及以上
     * @return 是否在 9.0 版本及以上
     */
    public static boolean isP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 是否在 10.0 版本及以上
     * @return 是否在 10.0 版本及以上
     */
    public static boolean isQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * 是否在 11.0 版本及以上
     * @return 是否在 11.0 版本及以上
     */
    public static boolean isR() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    /**
     * 转换 SDK 版本 convertSDKVersion(14) = Android 4.0.0-2
     * @return SDK 版本
     */
    public static String convertSDKVersion() {
        return convertSDKVersion(Build.VERSION.SDK_INT);
    }

    /**
     * 转换 SDK 版本 convertSDKVersion(14) = Android 4.0.0-2
     * @param sdkVersion SDK 版本
     * @return SDK 版本
     */
    public static String convertSDKVersion(final int sdkVersion) {
        switch (sdkVersion) {
            case 1:
                return "Android 1.0";
            case 2:
                return "Android 1.1";
            case 3:
                return "Android 1.5";
            case 4:
                return "Android 1.6";
            case 5:
                return "Android 2.0";
            case 6:
                return "Android 2.0.1";
            case 7:
                return "Android 2.1.x";
            case 8:
                return "Android 2.2.x";
            case 9:
                return "Android 2.3.0-2";
            case 10:
                return "Android 2.3.3-4";
            case 11:
                return "Android 3.0.x";
            case 12:
                return "Android 3.1.x";
            case 13:
                return "Android 3.2";
            case 14:
                return "Android 4.0.0-2";
            case 15:
                return "Android 4.0.3-4";
            case 16:
                return "Android 4.1.x";
            case 17:
                return "Android 4.2.x";
            case 18:
                return "Android 4.3";
            case 19:
                return "Android 4.4";
            case 20:
                return "Android 4.4W";
            case 21:
                return "Android 5.0";
            case 22:
                return "Android 5.1";
            case 23:
                return "Android 6.0";
            case 24:
                return "Android 7.0";
            case 25:
                return "Android 7.1.1";
            case 26:
                return "Android 8.0";
            case 27:
                return "Android 8.1";
            case 28:
                return "Android 9.0";
            case 29:
                return "Android 10.0";
            case 30:
                return "Android 11.0";
        }
        return "unknown";
    }
}