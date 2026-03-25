# Check GL Version

// Example check in Kotlin
val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
val configInfo = activityManager.deviceConfigurationInfo
if (configInfo.reqGlEsVersion >= 0x30002) {
// Enable high-end graphics
}

