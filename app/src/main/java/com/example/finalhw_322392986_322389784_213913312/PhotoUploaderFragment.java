package com.example.finalhw_322392986_322389784_213913312;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalhw_322392986_322389784_213913312.logic_model.Activity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoUploaderFragment extends Fragment {

    private static final int REQUEST_GALLERY = 1;

    private Button addPhotoBtn;
    private RecyclerView photoRecyclerView;
    private PhotoAdapter adapter;
    private List<String> photoBase64List = new ArrayList<>();

    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private String activityId; // For Firestore use later

    public static PhotoUploaderFragment newInstance(String activityId) {
        PhotoUploaderFragment fragment = new PhotoUploaderFragment();
        Bundle args = new Bundle();
        args.putString("activityId", activityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_uploader, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            activityId = getArguments().getString("activityId");
        }

        addPhotoBtn = view.findViewById(R.id.btnAddPhoto);
        photoRecyclerView = view.findViewById(R.id.photoRecyclerView);

        photoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(photoBase64List);
        photoRecyclerView.setAdapter(adapter);

        // === Register result launchers ===
        cameraResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap image = (Bitmap) extras.get("data");
                        addPhotoToList(encodeToBase64(image));
                    }
                });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openCamera();
                    } else {
                        Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        addPhotoBtn.setOnClickListener(v -> showPhotoOptionsDialog());

        // === Fetch photos from Firestore ===
        fetchPhotosFromFirestore();
    }

    private void showPhotoOptionsDialog() {
        String[] options = {"Camera", "Gallery"};
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Add Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
                        } else {
                            openCamera();
                        }
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraResultLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // initialize bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                // a call to the method that saves the photo that includes a call to the method that encodes the photos
                addPhotoToList(encodeToBase64(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // this method is used to save the base64 code to the FireBase
    private void addPhotoToList(String base64Image) {
        photoBase64List.add(base64Image);
        adapter.notifyItemInserted(photoBase64List.size() - 1);
        // Save updated photo list to Firestore
        FirebaseFirestore.getInstance()
                .collection("activities")
                .document(activityId)
                .update("photos", photoBase64List);
    }

    // method to encode photos to base64
    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // compress the bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        // this is the encoded string and and it is returned (this is what is sent to the addPhotosToList method)
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Load existing photos from Firestore and update recycler
    private void fetchPhotosFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("activities")
                .document(activityId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        List<String> photos = (List<String>) doc.get("photos");
                        if (photos != null) {
                            photoBase64List.clear();
                            photoBase64List.addAll(photos);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load photos", Toast.LENGTH_SHORT).show();
                });
    }
}
