import 'dart:async';

import 'package:flutter/services.dart';

class PackageUsageStats {
  static const MethodChannel _channel = MethodChannel('package_usage_stats');

  static Future<PermissionStatus?> checkPermissionStatus() async {
    final result = await _channel.invokeMethod("checkPermissionStatus");

    return PermissionStatus.values.firstWhere((e) => e.name == result);
  }

  static Future<bool> openAppUsageSettings() async {
    final result = await _channel.invokeMethod("openAppUsageSettings");

    return result;
  }
}

enum PermissionStatus {
  denied,
  granted,
}
