package com.stunware.package_usage_stats;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import io.flutter.Log;

public class UsagePermissionMonitor {
    private final Context context;
    private final AppOpsManager appOpsManager;
    private final Handler handler;
    private boolean _isListening;
    private Boolean lastValue;

    @Nullable
    private Consumer<Boolean> onDataChangedConsumer;

    public UsagePermissionMonitor(Context context) {
        this.context = context;
        appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        handler = new Handler();
    }

    public void startListening(Consumer<Boolean> callback) {
        appOpsManager.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS, context.getPackageName(), usageOpListener);
        _isListening = true;

        onDataChangedConsumer = callback;
    }

    public void stopListening() {
        lastValue = null;
        _isListening = false;
        appOpsManager.stopWatchingMode(usageOpListener);
        handler.removeCallbacks(checkUsagePermission);
        onDataChangedConsumer = null;
    }

    public boolean isListening() {
        return _isListening;
    }

    private final AppOpsManager.OnOpChangedListener usageOpListener = new AppOpsManager.OnOpChangedListener() {
        @Override
        public void onOpChanged(String op, String packageName) {
            // Android sometimes sets packageName to null
            if (packageName == null || context.getPackageName().equals(packageName)) {
                // Android actually notifies us of changes to ops other than the one we registered for, so filtering them out
                if (AppOpsManager.OPSTR_GET_USAGE_STATS.equals(op)) {
                    // We're not in main thread, so post to main thread queue
                    handler.post(checkUsagePermission);
                }
            }
        }
    };

    private final Runnable checkUsagePermission = new Runnable() {
        @Override
        public void run() {
            if (_isListening) {
                int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
                boolean enabled = mode == AppOpsManager.MODE_ALLOWED;

                // Each change to the permission results in two callbacks instead of one.
                // Filtering out the duplicates.
                if (lastValue == null || lastValue != enabled) {
                    lastValue = enabled;

                    onDataChangedConsumer.accept(lastValue);

                    // TODO: Do something with the result
                    Log.i(UsagePermissionMonitor.class.getSimpleName(), "Usage permission changed: " + enabled);
                }
            }
        }
    };


}