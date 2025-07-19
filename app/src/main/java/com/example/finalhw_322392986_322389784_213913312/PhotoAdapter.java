package com.example.finalhw_322392986_322389784_213913312;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<String> base64Images;

    public PhotoAdapter(List<String> base64Images) {
        this.base64Images = base64Images;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }


    // here we get the encoded base64 code and decoded it
    // we do this to display the photos in the recyclerview
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String base64 = base64Images.get(position);
        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
        // initialize bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
       // set bitmap on imageView
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return base64Images.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
