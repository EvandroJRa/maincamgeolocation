package mai.maincamgeolocation.view;

import java.util.List;
import mai.maincamgeolocation.model.Photo;

public interface MainView {
    void showPhotos(List<Photo> photos);  // Exibir lista de fotos
    void showError(String message);      // Exibir mensagens de erro
}

