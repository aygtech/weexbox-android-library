import android.content.pm.PackageManager
import android.os.Build
import com.weexbox.core.WeexBoxEngine

object VersionUtil {

    val hasJellyBeanMR2 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2

    val hasHoneycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

    val hasJellyBean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN

    val hasJellyBeanMR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1

    val hasKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    val hasLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

    val hasM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 获取版本号

     * @return 当前应用的版本号
     */
    val appVersionName: String?
        get() {
            val manager = WeexBoxEngine.application.packageManager
            try {
                val info = manager.getPackageInfo(WeexBoxEngine.application.packageName, 0)
                val version = info.versionName
                return version
            } catch (e: PackageManager.NameNotFoundException) {

            }

            return null
        }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0

     * @param version1
     * *
     * @param version2
     * *
     * @return
     */
    fun compareVersion(v1: String?, v2: String?): Int {
        val version1 = v1 ?: "0"
        val version2 = v2 ?: "0"
        val versionArray1: List<String> = version1.split(".")//kotlin 中不需要正则匹配，不用如此"\\."；
        val versionArray2: List<String> = version2.split(".")
        var idx = 0
        val minLength = Math.min(versionArray1.size, versionArray2.size)//取最小长度值
        var diff = 0
        while (idx < minLength) {
            diff = versionArray1[idx].length - versionArray2[idx].length//先比较长度
            if (diff == 0) {
                diff = versionArray1[idx].compareTo(versionArray2[idx])//再比较字符
                if (diff == 0) {
                    ++idx
                } else {
                    break
                }
            } else {
                break
            }
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = if (diff != 0) diff else versionArray1.size - versionArray2.size
        return diff
    }
}