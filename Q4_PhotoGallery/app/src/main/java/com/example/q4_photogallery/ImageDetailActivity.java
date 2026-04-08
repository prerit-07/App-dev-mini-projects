package com.example.q4_photogallery; // UPDATE THIS

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView detailImageView;
    TextView tvDetails;
    Button btnDelete;
    DocumentFile documentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        detailImageView = findViewById(R.id.detailImageView);
        tvDetails = findViewById(R.id.tvDetails);
        btnDelete = findViewById(R.id.btnDelete);

        String uriString = getIntent().getStringExtra("IMAGE_URI");
        if (uriString != null) {
            Uri imageUri = Uri.parse(uriString);
            documentFile = DocumentFile.fromSingleUri(this, imageUri);

            if (documentFile != null) displayImageAndDetails(imageUri);
        }

        // Requirement c(ii): Delete button with Confirmation Dialog
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this image permanently?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (documentFile != null && documentFile.delete()) {
                            Toast.makeText(this, "Image Deleted!", Toast.LENGTH_SHORT).show();
                            // Brought back to Image gallery view
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void displayImageAndDetails(Uri uri) {
        // Load Image (OOM Safe)
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
            detailImageView.setImageBitmap(bmp);
            if (is != null) is.close();
        } catch (Exception e) { e.printStackTrace(); }

        // Requirement c(i): View image name, path, size, date taken
        long sizeInKB = documentFile.length() / 1024;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String dateString = sdf.format(new Date(documentFile.lastModified()));

        String details = "Name: " + documentFile.getName() + "\n\n" +
                "Path: " + uri.getPath() + "\n\n" +
                "Size: " + sizeInKB + " KB\n\n" +
                "Date Taken: " + dateString;

        tvDetails.setText(details);
    }
}