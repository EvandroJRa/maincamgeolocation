package mai.maincamgeolocation.view;

import static mai.maincamgeolocation.view.GalleryActivity.STORAGE_REQUEST_CODE;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;

import mai.maincamgeolocation.R;
import mai.maincamgeolocation.presenter.MainPresenter;
import mai.maincamgeolocation.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;

    private Camera camera;
    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;
    private Button captureButton;
    private Button openGalleryButton;
    private MainPresenter presenter;

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
            if (PermissionUtils.hasPermission(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            } else {
                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_REQUEST_CODE);
            }
        });

        // Configurar visualização da câmera
        configureCameraPreview();
    }
    private void showPermissionSettingsDialog(String permissionType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão Necessária")
                .setMessage("A permissão para acessar a " + permissionType + " é necessária para o funcionamento deste aplicativo. Você pode concedê-la nas configurações do dispositivo.")
                .setPositiveButton("Abrir Configurações", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
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
    private void openCamera() {
        try {
            if (camera == null) {
                camera = Camera.open();
            }
            camera.setPreviewDisplay(surfaceHolder);
            configureCameraParameters();
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao abrir a câmera.", Toast.LENGTH_SHORT).show();
        }
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

    private void capturePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, (data, camera) -> {
                Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();

                saveImageToGallery(data);

                camera.startPreview(); // Reiniciar o preview da câmera
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                    !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showPermissionSettingsDialog("câmera");
            } else {
                Toast.makeText(this, "Permissão da câmera necessária.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M &&
                    !shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                showPermissionSettingsDialog("galeria");
            } else {
                Toast.makeText(this, "Permissão da galeria necessária.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openGallery() {
        // Aqui você pode iniciar a `GalleryActivity`
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Chamar o método savePhoto do Presenter
            presenter.savePhoto(data, this);
        }
    }
    private void handleCaptureButton() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!PermissionUtils.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
                return; // Evita continuar sem permissão
            }
        }

        if (PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            capturePhoto();
        } else {
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }


    private void saveImageToGallery(byte[] imageData) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "foto_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MeuApp");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                outputStream.write(imageData);
                outputStream.close();
                Toast.makeText(this, "Foto salva na galeria", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show();
        }
    }


}
