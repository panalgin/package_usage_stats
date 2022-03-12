import 'dart:async';

import 'package:flutter/services.dart';

class PackageUsageStats {
  static const MethodChannel _channel = MethodChannel('package_usage_stats');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<PermissionStatus?> get checkPermissionStatus async {
    final PermissionStatus? status =
        await _channel.invokeMethod("checkPermissionStatus");

    return status;
  }
}

enum PermissionStatus {
  denied,
  granted,
}
