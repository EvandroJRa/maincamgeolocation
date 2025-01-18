package mai.maincamgeolocation.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

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
        // Obter localização
        Location location = LocationUtils.getCurrentLocation(context);
        if (location != null) {
            Photo newPhoto = new Photo(
                    "path/to/photo", // Substituir pelo caminho real
                    location.getLatitude(),
                    location.getLongitude(),
                    String.valueOf(System.currentTimeMillis()) // Timestamp
            );
            repository.savePhoto(newPhoto);
            loadPhotos(); // Atualizar a lista de fotos
        } else {
            view.showError("Não foi possível obter a localização.");
        }
    }
    public void deletePhoto(String filePath) {
        repository.deletePhoto(filePath);
        loadPhotos(); // Atualizar a lista
    }

}
