import 'dart:async';

import 'package:flutter/services.dart';

/// A static class for accessing PACKAGE_USAGE_STATS API of Android.
class PackageUsageStats {
  static const MethodChannel _channel =
      MethodChannel('package_usage_stats/method_channel');
  static const EventChannel _eventChannel =
      EventChannel('package_usage_stats/event_channel');

  /// Checks the permission status for android.manifest.PACKAGE_USAGE_STATS
  /// @returns {Promise<boolean>} Returns true if the permission is granted, false otherwise.
  static Future<bool> checkPermissionStatus() async {
    final result = await _channel.invokeMethod("checkPermissionStatus");

    return result;
  }

  /// Opens an activity window where the user can see all the apps those request the PACKAGE_USAGE_STATS permission.
  /// Upon selecting the desired app, user can grant or deny the permission. Because PACKAGE_USAGE_STATS considered
  /// as a dangerous permission, it is the only way to get the permission on Android.
  static Future<bool> openAppUsageSettings() async {
    final result = await _channel.invokeMethod("openAppUsageSettings");

    return result;
  }

  /// Starts listening for the PACKAGE_USAGE_STATS permission status change. This method is useful when you want to
  /// know the status of the permission at any point of time, including the time when the app is backgrounded.
  /// Returns a stream of [bool?] events, indicating the current status of the permission.
  static Stream<bool?> get onPermissionStatusChanged => _eventChannel
      .receiveBroadcastStream()
      .map((event) => event is bool ? event : null);
}
