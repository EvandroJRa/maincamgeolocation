package mai.maincamgeolocation.view;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import mai.maincamgeolocation.R;
import mai.maincamgeolocation.model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photos;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String filePath);
    }

    public PhotoAdapter(List<Photo> photos, OnDeleteClickListener listener) {
        this.photos = photos;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);

        // Exibir a miniatura da foto ou um placeholder
        if (photo.getFilePath() == null || photo.getFilePath().isEmpty()) {
            holder.photoThumbnail.setImageResource(R.drawable.ic_photo_placeholder);
        } else {
            holder.photoThumbnail.setImageURI(Uri.parse(photo.getFilePath()));
        }

        // Configurar os detalhes
        holder.photoName.setText(photo.getFilePath());
        holder.photoDetails.setText(photo.getTimestamp());

        // Configurar o botão de exclusão
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClickListener.onDeleteClick(photo.getFilePath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoThumbnail;
        TextView photoName;
        TextView photoDetails;
        Button deleteButton;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
            photoName = itemView.findViewById(R.id.photoName);
            photoDetails = itemView.findViewById(R.id.photoDetails);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
