package com.stunware.package_usage_stats;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * PackageUsageStatsPlugin
 */
public class PackageUsageStatsPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel _methodChannel;
    private EventChannel _eventChannel;
    private ActivityPluginBinding _activityBinding;
    private FlutterPluginBinding _flutterBinding;
    private UsagePermissionMonitor _monitor;

    private final String debugTag = "PackageUsageStats/Native";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        _flutterBinding = flutterPluginBinding;

        _methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "package_usage_stats/method_channel");
        _methodChannel.setMethodCallHandler(this);

        _eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "package_usage_stats/event_channel");
        _eventChannel.setStreamHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "checkPermissionStatus":
                boolean granted;

                Context context = _flutterBinding.getApplicationContext();

                AppOpsManager appOps = (AppOpsManager) context
                        .getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        android.os.Process.myUid(), context.getPackageName());

                if (mode == AppOpsManager.MODE_DEFAULT) {
                    granted = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
                } else {
                    granted = (mode == AppOpsManager.MODE_ALLOWED);
                }

                result.success(granted);
                break;
            case "openAppUsageSettings":
                try {
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(getActivity().getApplicationContext(), intent, null);
                    result.success(true);
                } catch (Exception ex) {
                    Log.d(debugTag, ex.toString());
                    result.success(false);
                }
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        _methodChannel.setMethodCallHandler(null);
        _methodChannel = null;

        if (_monitor != null) {
            _monitor.stopListening();
            _monitor = null;
        }

        _eventChannel.setStreamHandler(null);
        _eventChannel = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        Log.d(debugTag, "onAttachedToActivity");
        _activityBinding = binding;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        Log.d(debugTag, "onDetachedFromActivityForConfigChanges");
        _activityBinding = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        Log.d(debugTag, "onReattachedToActivityForConfigChanges");
        _activityBinding = binding;
    }

    @Override
    public void onDetachedFromActivity() {
        Log.d(debugTag, "onDetachedFromActivity");
        _activityBinding = null;
    }

    public Activity getActivity() {
        return (_activityBinding != null) ? _activityBinding.getActivity() : null;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink sink) {
        createListener(sink);
    }

    @Override
    public void onCancel(Object arguments) {
        if (_monitor != null) {
            _monitor.stopListening();
            _monitor = null;
        }
    }

    private void createListener(EventChannel.EventSink sink) {
        if (_monitor == null) {
            _monitor = new UsagePermissionMonitor(_flutterBinding.getApplicationContext());
        }

        if (!_monitor.isListening()) {
            _monitor.startListening(sink::success);
        }
    }
}
