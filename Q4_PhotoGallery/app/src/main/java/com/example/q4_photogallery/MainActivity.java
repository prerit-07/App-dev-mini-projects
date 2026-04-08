package com.example.q4_photogallery; // APNA PACKAGE NAME ZAROOR CHECK KARNA

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnChooseFolder, btnTakePic;
    TextView tvFolderName;
    GridView gridView;
    Uri chosenFolderUri = null;
    File tempCameraFile;
    ArrayList<DocumentFile> imageFiles = new ArrayList<>();
    GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChooseFolder = findViewById(R.id.btnChooseFolder);
        btnTakePic = findViewById(R.id.btnTakePic);
        tvFolderName = findViewById(R.id.tvFolderName);
        gridView = findViewById(R.id.gridView);

        adapter = new GalleryAdapter();
        gridView.setAdapter(adapter);

        btnChooseFolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            folderLauncher.launch(intent);
        });

        // ==========================================
        // BUG FIX: CAMERA PERMISSION LOGIC ADDED
        // ==========================================
        btnTakePic.setOnClickListener(v -> {
            if (chosenFolderUri == null) {
                Toast.makeText(this, "Please choose a folder first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check karo ki Camera permission mili hui hai ya nahi
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Agar permission hai, toh camera kholo
            } else {
                // Agar nahi hai, toh user se popup me permission mango
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent detailIntent = new Intent(MainActivity.this, ImageDetailActivity.class);
            detailIntent.putExtra("IMAGE_URI", imageFiles.get(position).getUri().toString());
            startActivity(detailIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chosenFolderUri != null) loadImagesFromFolder();
    }

    // Permission maangne wala popup launcher
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera(); // Allow dabaya toh camera kholo
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos!", Toast.LENGTH_LONG).show();
                }
            }
    );

    ActivityResultLauncher<Intent> folderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    chosenFolderUri = result.getData().getData();
                    getContentResolver().takePersistableUriPermission(chosenFolderUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    DocumentFile pickedDir = DocumentFile.fromTreeUri(this, chosenFolderUri);
                    tvFolderName.setText("Selected Folder: " + pickedDir.getName());
                    loadImagesFromFolder();
                }
            }
    );

    private void openCamera() {
        tempCameraFile = new File(getExternalCacheDir(), "temp_photo.jpg");
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", tempCameraFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) saveTempFileToChosenFolder();
            }
    );

    private void saveTempFileToChosenFolder() {
        try {
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, chosenFolderUri);
            String newFileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            DocumentFile newImage = pickedDir.createFile("image/jpeg", newFileName);

            OutputStream out = getContentResolver().openOutputStream(newImage.getUri());
            InputStream in = new FileInputStream(tempCameraFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) { out.write(buffer, 0, len); }
            in.close(); out.close();

            tempCameraFile.delete();
            loadImagesFromFolder();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadImagesFromFolder() {
        imageFiles.clear();
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, chosenFolderUri);
        if (pickedDir != null) {
            for (DocumentFile file : pickedDir.listFiles()) {
                if (file.getType() != null && file.getType().startsWith("image/")) {
                    imageFiles.add(file);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    class GalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() { return imageFiles.size(); }
        @Override
        public Object getItem(int position) { return imageFiles.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }
            ImageView img = convertView.findViewById(R.id.imgItem);

            try {
                InputStream is = getContentResolver().openInputStream(imageFiles.get(position).getUri());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
                img.setImageBitmap(bmp);
                is.close();
            } catch (Exception e) { e.printStackTrace(); }
            return convertView;
        }
    }
}