import 'dart:async';

import 'package:flutter/services.dart';

class PackageUsageStats {
  static const MethodChannel _channel = MethodChannel('package_usage_stats');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<PermissionStatus?> checkPermissionStatus() async {
    final result = await _channel.invokeMethod("checkPermissionStatus");

    return PermissionStatus.values.firstWhere((e) => e.name == result);
  }
}

enum PermissionStatus {
  denied,
  granted,
}
