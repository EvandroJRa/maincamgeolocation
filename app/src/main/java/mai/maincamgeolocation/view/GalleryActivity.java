package mai.maincamgeolocation.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import mai.maincamgeolocation.R;
import mai.maincamgeolocation.model.Photo;
import mai.maincamgeolocation.presenter.MainPresenter;
import mai.maincamgeolocation.utils.PermissionUtils;

public class GalleryActivity extends AppCompatActivity implements MainView {

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private MainPresenter presenter;
    private Button deleteButton;
    private String selectedPhotoPath; // Caminho da foto selecionada

    public static final int STORAGE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (!PermissionUtils.hasPermission(this, Manifest.permission.READ_MEDIA_IMAGES)) {
            PermissionUtils.requestPermission(
                    this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    STORAGE_REQUEST_CODE
            );
        } else {
            // Carregar a galeria apenas se a permissão foi concedida
            loadGallery();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(grantResults)) {
                loadGallery();
            } else {
                Toast.makeText(this, "Permissão necessária para acessar a galeria.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeGallery() {
        // Configurar RecyclerView e carregar fotos
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        presenter.loadPhotos(); // Carregar as fotos do banco de dados ou armazenamento

        // Configurar botão de exclusão
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            if (selectedPhotoPath != null) {
                presenter.deletePhoto(selectedPhotoPath);
                selectedPhotoPath = null;
                deleteButton.setEnabled(false); // Desativar botão após exclusão
            }
        });
    }
    private void loadGallery() {
        // Sua lógica para carregar as imagens da galeria
        presenter.loadPhotos();
    }
    @Override
    public void showPhotos(List<Photo> photos) {
        Log.d("GalleryActivity", "Total de fotos recebidas para exibição: " + photos.size());

        for (Photo photo : photos) {
            Log.d("GalleryActivity", "Foto: " + photo.getFilePath());
        }

        photoAdapter = new PhotoAdapter(photos, filePath -> {
            selectedPhotoPath = filePath;
            deleteButton.setEnabled(true);
        });
        photoRecyclerView.setAdapter(photoAdapter);
    }
    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
