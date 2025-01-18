package mai.maincamgeolocation.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mai.maincamgeolocation.R;
import mai.maincamgeolocation.model.Photo;
import mai.maincamgeolocation.presenter.MainPresenter;
import mai.maincamgeolocation.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final int CAMERA_REQUEST_CODE = 101;

    private Camera camera;
    private SurfaceView cameraPreview;
    private SurfaceHolder surfaceHolder;

    private MainPresenter presenter;
    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private List<Photo> photoList;
    private Button captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes do layout
        cameraPreview = findViewById(R.id.cameraPreview);
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        captureButton = findViewById(R.id.captureButton);

        // Configurar o Presenter
        presenter = new MainPresenter(this, this);

        // Configurar RecyclerView
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList, new PhotoAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(String filePath) {
                presenter.deletePhoto(filePath);
            }
        });
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        photoRecyclerView.setAdapter(photoAdapter);

        // Configurar botão de captura
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.CAMERA) &&
                        PermissionUtils.hasPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    capturePhoto();
                } else {
                    PermissionUtils.requestPermission(
                            MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                            CAMERA_REQUEST_CODE
                    );
                }
            }
        });

        // Configurar visualização da câmera
        configureCameraPreview();

        // Carregar fotos salvas
        presenter.loadPhotos();
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
                    camera.stopPreview();
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    private void configureCameraParameters() {
        Camera.Parameters parameters = camera.getParameters();

        // Configurar resolução do preview
        Camera.Size optimalSize = getOptimalPreviewSize(parameters.getSupportedPreviewSizes());
        if (optimalSize != null) {
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        }

        // Configurar foco contínuo
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes) {
        Camera.Size optimalSize = null;
        for (Camera.Size size : sizes) {
            if (size.width <= 640 && size.height <= 480) { // Resolução de exemplo
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }

    private void openCameraPreview(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);

            // Configurar os parâmetros da câmera
            configureCameraParameters();

            // Ajustar orientação
            camera.setDisplayOrientation(90);

            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao iniciar a câmera", Toast.LENGTH_SHORT).show();
        }
    }

    private void capturePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, (data, camera) -> {
                // Processar ou salvar a foto capturada
                Toast.makeText(this, "Foto capturada", Toast.LENGTH_SHORT).show();
                camera.startPreview(); // Reiniciar a visualização após capturar
            });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            presenter.savePhoto(data, this);
        }
    }

    @Override
    public void showPhotos(List<Photo> photos) {
        photoList.clear();
        photoList.addAll(photos);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
