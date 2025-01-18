package mai.maincamgeolocation.view;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import mai.maincamgeolocation.R;
import mai.maincamgeolocation.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;

    private Camera camera;
    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;
    private Button captureButton;
    private Button openGalleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes
        cameraPreview = findViewById(R.id.cameraPreview);
        captureButton = findViewById(R.id.captureButton);
        openGalleryButton = findViewById(R.id.openGalleryButton);

        // Configurar botão de captura
        captureButton.setOnClickListener(v -> handleCaptureButton());

        // Configurar botão de galeria
        openGalleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(intent);
        });

        // Configurar visualização da câmera
        configureCameraPreview();
    }

    private void configureCameraPreview() {
        surfaceHolder = cameraPreview.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                openCameraPreview(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera != null) {
                    try {
                        camera.stopPreview();
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCamera();
            }
        });
    }

    private void openCameraPreview(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
            configureCameraParameters();
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao iniciar a câmera", Toast.LENGTH_SHORT).show();
        }
    }

    private void configureCameraParameters() {
        if (camera == null) return;

        Camera.Parameters parameters = camera.getParameters();

        // Configurar resolução e foco
        Camera.Size optimalSize = getOptimalPreviewSize(parameters.getSupportedPreviewSizes());
        if (optimalSize != null) {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size getOptimalPreviewSize(java.util.List<Camera.Size> sizes) {
        Camera.Size optimalSize = null;
        for (Camera.Size size : sizes) {
            if (size.width <= 640 && size.height <= 480) {
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }

    private void handleCaptureButton() {
        if (PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            capturePhoto();
        } else {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void capturePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, (data, camera) -> {
                Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
                camera.startPreview(); // Reiniciar o preview
            });
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(grantResults)) {
                capturePhoto();
            } else {
                Toast.makeText(this, "Permissões necessárias não foram concedidas.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}
