package com.example.q4_photogallery; // APNA PACKAGE NAME MATCH KAR LENA

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnCapture;
    GridView gridView;
    File currentPhotoFile;
    File[] allPhotos; // Phone mein save saari photos ki list
    PhotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapture = findViewById(R.id.btnCapturePhoto);
        gridView = findViewById(R.id.photoGridView);

        adapter = new PhotoAdapter();
        gridView.setAdapter(adapter);

        // Jab app khule, purani photos load kar lo
        loadPhotos();

        // Take Photo Button ka kaam
        btnCapture.setOnClickListener(v -> openCamera());

        // Jab user kisi photo par click kare (View aur Delete karne ke liye)
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            showPhotoDetailDialog(allPhotos[position]);
        });
    }

    // ==========================================
    // 1. CAMERA SE PHOTO LENA
    // ==========================================
    private void openCamera() {
        // Ek khali file banao jahan photo save hogi
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        currentPhotoFile = new File(storageDirectory, fileName);

        // FileProvider se us file ka secure link banao
        Uri imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", currentPhotoFile);

        // Camera open karne ka command
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(cameraIntent);
    }

    // Camera jab photo kheench kar wapas aaye
    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show();
                    loadPhotos(); // Nayi photo aane par gallery refresh karo
                }
            }
    );

    // ==========================================
    // 2. GALLERY MEIN PHOTOS LOAD KARNA
    // ==========================================
    private void loadPhotos() {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDirectory != null) {
            allPhotos = storageDirectory.listFiles(); // Folder se saari files utha li
            adapter.notifyDataSetChanged(); // Grid ko update kiya
        }
    }

    // ==========================================
    // 3. PHOTO DETAILS & DELETE DIALOG
    // ==========================================
    private void showPhotoDetailDialog(File photoFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo Detail");
        builder.setMessage("Path: " + photoFile.getName() + "\nSize: " + (photoFile.length() / 1024) + " KB");

        // Badi photo dikhane ke liye dialog mein ImageView lagaya
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(Uri.fromFile(photoFile));
        imageView.setPadding(20, 20, 20, 20);
        builder.setView(imageView);

        // Delete Button
        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (photoFile.delete()) {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                loadPhotos(); // Delete hone ke baad gallery refresh karo
            }
        });

        // Close Button
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // ==========================================
    // 4. CUSTOM ADAPTER (Grid ko design se jodne ke liye)
    // ==========================================
    class PhotoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return (allPhotos != null) ? allPhotos.length : 0;
        }

        @Override
        public Object getItem(int position) { return allPhotos[position]; }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // grid_item.xml ko utha kar yahan load karo
                convertView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }

            ImageView imgView = convertView.findViewById(R.id.gridImageView);
            // File ko image URI mein badal kar box mein set kar do
            imgView.setImageURI(Uri.fromFile(allPhotos[position]));

            return convertView;
        }
    }
}