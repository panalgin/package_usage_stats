import 'package:flutter/material.dart';
import 'dart:async';

import 'package:package_usage_stats/package_usage_stats.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _permissionStatus = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    bool isGranted = await PackageUsageStats.checkPermissionStatus();

    PackageUsageStats.onPermissionStatusChanged.listen((event) {
      setState(() {
        if (event != null) {
          _permissionStatus =
              event ? "Granted In Background" : "Denied In Background";
        } else {
          _permissionStatus = "Background Detection Error";
        }
      });
    });

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _permissionStatus = isGranted ? 'Granted' : 'Denied';
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Center(
              child: Text("PackageUsageStats permission: $_permissionStatus"),
            ),
            ElevatedButton(
              onPressed: () => PackageUsageStats.openAppUsageSettings(),
              child: const Text("Open App Usage Settings"),
            ),
          ],
        ),
      ),
    );
  }
}
