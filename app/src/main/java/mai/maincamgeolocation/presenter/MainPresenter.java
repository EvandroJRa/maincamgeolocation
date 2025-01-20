package mai.maincamgeolocation.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import mai.maincamgeolocation.model.Photo;
import mai.maincamgeolocation.model.PhotoRepository;
import mai.maincamgeolocation.utils.LocationUtils;
import mai.maincamgeolocation.view.MainView;

public class MainPresenter {
    private MainView view;
    private PhotoRepository repository;

    public MainPresenter(MainView view, Context context) {
        this.view = view;
        this.repository = new PhotoRepository(context);
    }

    public void loadPhotos() {
        List<Photo> photos = repository.getAllPhotos();
        if (photos.isEmpty()) {
            view.showError("Nenhuma foto encontrada.");
        } else {
            view.showPhotos(photos);
        }
    }
    public void savePhoto(Intent data, Context context) {
        if (data != null && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String filePath = saveBitmapToFile(photo, context);

            if (filePath != null) {
                Location location = LocationUtils.getCurrentLocation(context);
                double latitude = (location != null) ? location.getLatitude() : 0.0;
                double longitude = (location != null) ? location.getLongitude() : 0.0;

                Log.d("MainPresenter", "Caminho da foto: " + filePath);
                Log.d("MainPresenter", "Coordenadas - Latitude: " + latitude + ", Longitude: " + longitude);

                Photo newPhoto = new Photo(filePath, latitude, longitude, String.valueOf(System.currentTimeMillis()));
                repository.savePhoto(newPhoto);
                view.showPhotos(repository.getAllPhotos());
            } else {
                Log.e("MainPresenter", "Erro ao salvar o Bitmap em arquivo.");
            }
        } else {
            Log.e("MainPresenter", "Dados inválidos recebidos da câmera.");
        }
    }

    private String saveBitmapToFile(Bitmap bitmap, Context context) {
        File directory = new File(context.getExternalFilesDir(null), "Photos");
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            Log.d("MainPresenter", "Diretório criado: " + created);
        }

        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File file = new File(directory, fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            Log.d("MainPresenter", "Foto salva em: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainPresenter", "Erro ao salvar a foto: " + e.getMessage());
            return null;
        }
    }

    public void deletePhoto(String filePath) {
        repository.deletePhoto(filePath);
        loadPhotos(); // Atualizar a lista
    }

}
