import 'dart:async';

import 'package:flutter/services.dart';

class PackageUsageStats {
  static const MethodChannel _channel =
      MethodChannel('package_usage_stats/method_channel');
  static const EventChannel _eventChannel =
      EventChannel('package_usage_stats/event_channel');

  static Future<bool> checkPermissionStatus() async {
    final result = await _channel.invokeMethod("checkPermissionStatus");

    return result;
  }

  static Future<bool> openAppUsageSettings() async {
    final result = await _channel.invokeMethod("openAppUsageSettings");

    return result;
  }

  static Stream<bool?> get onPermissionStatusChanged => _eventChannel
      .receiveBroadcastStream()
      .map((event) => event is bool ? event : null);
}

enum PermissionStatus {
  denied,
  granted,
}
