package mai.maincamgeolocation.view;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import mai.maincamgeolocation.R;
import mai.maincamgeolocation.model.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Photo> photos;
    private int selectedPosition = -1; // Nenhuma imagem selecionada inicialmente
    private final OnPhotoSelectListener onPhotoSelectListener;

    public interface OnPhotoSelectListener {
        void onPhotoSelected(String filePath);
    }

    public PhotoAdapter(List<Photo> photos, OnPhotoSelectListener onPhotoSelectListener) {
        this.photos = photos;
        this.onPhotoSelectListener = onPhotoSelectListener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        // Obter a posição atual do item
        int currentPosition = holder.getAdapterPosition();
        if (currentPosition == RecyclerView.NO_POSITION) {
            return; // Evitar problemas com posições inválidas
        }

        // Recuperar a foto da posição atual
        Photo photo = photos.get(currentPosition);

        // Verificar e carregar a imagem
        if (photo.getFilePath() != null && !photo.getFilePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(photo.getFilePath())
                    .placeholder(R.drawable.ic_photo_placeholder) // Placeholder enquanto carrega
                    .error(R.drawable.ic_photo_placeholder)      // Placeholder em caso de erro
                    .into(holder.photoThumbnail);
        } else {
            holder.photoThumbnail.setImageResource(R.drawable.ic_photo_placeholder);
        }

        // Configurar os detalhes da foto
        holder.photoDetails.setText(photo.getTimestamp());

        // Configurar seleção
        holder.itemView.setSelected(selectedPosition == currentPosition);
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = currentPosition; // Atualizar a posição selecionada
            notifyDataSetChanged();            // Atualizar a interface
            if (onPhotoSelectListener != null) {
                onPhotoSelectListener.onPhotoSelected(photo.getFilePath());
            }
        });
    }


    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoThumbnail;
        TextView photoDetails;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoThumbnail = itemView.findViewById(R.id.photoThumbnail);
            photoDetails = itemView.findViewById(R.id.photoDetails);
        }
    }
}
