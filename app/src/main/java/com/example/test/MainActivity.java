package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.Set;

public class MainActivity extends FragmentActivity {
    public static final int REQUEST_MEDIA_PROJECTION = 18;
    private static final int REQUEST_OVERLAY = 4444;
    private static final String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {
                requestCapturePermission();
            }
        } else {
            Toast.makeText(this, "不支持6.0以下手机", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void requestCapturePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            return;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, String.valueOf(requestCode));
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    FloatWindowsService.setResultData(data);
                    startService(new Intent(getApplicationContext(), FloatWindowsService.class));
                }
                break;
            case REQUEST_OVERLAY:
                if (!Settings.canDrawOverlays(this)) {//有无悬浮窗权限
                    Toast.makeText(this, "需要悬浮窗权限才能使用", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    requestCapturePermission();
                }
        }
    }

}
