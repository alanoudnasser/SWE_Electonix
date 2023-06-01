package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements  AdapterCallback{
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView welcomeText;
    Button  deleteButton, addButtonInPopup, chooseImageButtonInPopup, logoutButton;
    FloatingActionButton addButton;
    DBHelper db;
    private RecyclerView recyclerView;
    private DeviceAdapter deviceAdapter;
    private List<Device> deviceList;
    View popupView;

    @Override
    public void onChange() {
        renderList();
    }

    public enum Page {
        HOME,
        RENTALS,
        MY_DEVICES
    }
    public Page page = Page.HOME;
    public void renderList() {
        deviceList = db.getAllDevices(page);
        System.out.println("DEVICES => " +deviceList.size());
        deviceAdapter = new DeviceAdapter(this, deviceList, page);
        deviceAdapter.setCallback(this);
        recyclerView.setAdapter(deviceAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHelper(this);
        setContentView(R.layout.activity_homepage);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceList = new ArrayList<>();
        welcomeText = findViewById(R.id.welcome_message);
        welcomeText.append(" " + User._username);
        addButton = findViewById(R.id.add_button);
        popupView = LayoutInflater.from(this).inflate(R.layout.add_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        addButtonInPopup = popupView.findViewById(R.id.add_button_popup);
        chooseImageButtonInPopup = popupView.findViewById(R.id.choose_image);
        final AlertDialog[] dialog = new AlertDialog[1];
        logoutButton = findViewById(R.id.logout);
        Button homeButton = findViewById(R.id.home_button);
        Button myRentalsButton = findViewById(R.id.my_rentals_button);
        Button myDevicesButton = findViewById(R.id.my_devices_button);

                homeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        page = Page.HOME;
                        renderList();
                    }
                });
        myRentalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = Page.RENTALS;
                renderList();
            }
        });
        myDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = Page.MY_DEVICES;
                renderList();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmationDialog.show(HomepageActivity.this, "Are you sure you want to logout", new ConfirmationDialog.ConfirmationDialogListener() {
                    @Override
                    public void onConfirm() {
                        User._username = null;
                        Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setView(popupView);
                dialog[0] = builder.create();
                dialog[0].show();
            }
        });


        addButtonInPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText deviceField = popupView.findViewById(R.id.device_field);
                EditText companyField = popupView.findViewById(R.id.company_field);
                EditText priceField = popupView.findViewById(R.id.price_field);
                ImageView imageView = popupView.findViewById(R.id.image);
                String name = deviceField.getText().toString().trim();
                String company = companyField.getText().toString().trim();
                String priceString = priceField.getText().toString().trim();
                if (name.isEmpty()) {
                    deviceField.setError("Device name is required");
                    deviceField.requestFocus();
                    return;
                }

                if (company.isEmpty()) {
                    companyField.setError("Company name is required");
                    companyField.requestFocus();
                    return;
                }

                if (priceString.isEmpty()) {
                    priceField.setError("Price is required");
                    priceField.requestFocus();
                    return;
                }
                int price;
                try {
                    price = Integer.parseInt(priceString);
                } catch (NumberFormatException e) {
                    priceField.setError("Invalid price format");
                    priceField.requestFocus();
                    return;
                }
                Drawable drawable = imageView.getDrawable();
                Bitmap imageBitmap = null;

                if (drawable instanceof BitmapDrawable) {
                    imageBitmap = ((BitmapDrawable) drawable).getBitmap();
                } else {
                    Toast.makeText(HomepageActivity.this, "Image IS REQUIRED", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] imageData = convertImageToByteArray(imageBitmap);
                Device device = new Device(-1, name, company, price,-1, imageData);
                db.insertDevice(device);
                page = Page.MY_DEVICES;
                renderList();
                dialog[0].dismiss();
            }

            private byte[] convertImageToByteArray(Bitmap imageBitmap) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            }
        });

        chooseImageButtonInPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }

        });


    } @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            ImageView imageView = popupView.findViewById(R.id.image);
            imageView.setImageURI(selectedImageUri);
        }
    }
}