package mai.maincamgeolocation.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mai.maincamgeolocation.R;
import mai.maincamgeolocation.model.Photo;
import mai.maincamgeolocation.presenter.MainPresenter;

public class GalleryActivity extends AppCompatActivity implements MainView {

    private RecyclerView photoRecyclerView;
    private PhotoAdapter photoAdapter;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Inicializar RecyclerView
        photoRecyclerView = findViewById(R.id.photoRecyclerView);
        photoRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Presenter
        presenter = new MainPresenter(this, this);

        // Carregar fotos
        presenter.loadPhotos();
    }

    @Override
    public void showPhotos(List<Photo> photos) {
        photoAdapter = new PhotoAdapter(photos, null); // Sem exclusão
        photoRecyclerView.setAdapter(photoAdapter);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
