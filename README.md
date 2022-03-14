# package_usage_stats

A simple (for now) package for accessing the current status of android.manifest.PACKAGE_USAGE_STATS permission.

`checkPermissionStatus()`  Returns true if the PACKAGE_USAGE_STATS permission is granted for the app, false otherwise

`openAppUsageSettings()` Opens an activity window where the user can see all the apps those request the PACKAGE_USAGE_STATS permission. Upon selecting the desired app, user can grant or deny the permission. Because PACKAGE_USAGE_STATS considered as a dangerous permission, it is the only way to get the permission on Android.

`onPermissionStatusChanged()`  Starts listening for the PACKAGE_USAGE_STATS permission status change. This method is useful when you want to know the status of the permission at any point of time, including the time when the app is backgrounded. Returns a stream of [bool?] events, indicating the current status of the permission

## Installation Requirements For Android

You need to add `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />` in your AndroidManifest.xml
Minimum required SDK is 21. This package will not work below < SDK 21, Lollipop

## For iOS

There is no direct equivalent of this API on iOS, however the new ScreenTime API might be considered for iOS devices, this package
does not support any iOS counterpart yet.