package com.example.universalscreenshot;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int SCREEN_CAPTURE_REQ_CODE = 100;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> checkPermissionsAndStart());
    }

    private void checkPermissionsAndStart() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            Toast.makeText(this, "Please allow 'Display over other apps' permission", Toast.LENGTH_LONG).show();
            return;
        }

        MediaProjectionManager projectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_CAPTURE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Intent serviceIntent = new Intent(this, ScreenshotService.class);
                serviceIntent.putExtra("code", resultCode);
                serviceIntent.putExtra("data", data);
                startForegroundService(serviceIntent);
                Toast.makeText(this, "Universal Floating Capture Activated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Permission Denied for Screen Capture", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
