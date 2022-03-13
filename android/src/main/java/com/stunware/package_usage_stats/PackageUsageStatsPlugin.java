package com.stunware.package_usage_stats;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * PackageUsageStatsPlugin
 */
public class PackageUsageStatsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel _channel;
    private ActivityPluginBinding _activityBinding;
    private final String debugTag = "PackageUsageStats/Native";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        _channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "package_usage_stats");
        _channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "checkPermissionStatus":
                result.success(PermissionStatus.granted.name());
                break;
            case "openAppUsageSettings":
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
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
        _channel.setMethodCallHandler(null);
        _channel = null;
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
}
