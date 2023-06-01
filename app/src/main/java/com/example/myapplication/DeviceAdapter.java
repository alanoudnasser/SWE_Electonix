package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceAdapter  extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> deviceList;
    private Context context;
    HomepageActivity.Page page;
    private  DBHelper db;
    public DeviceAdapter(Context context, List<Device> deviceList, HomepageActivity.Page page) {
        this.context = context;
        this.deviceList = deviceList;
        this.page= page;
        db = new DBHelper(context);
    }

    @NonNull
    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent, false);
        return new DeviceViewHolder(itemView);
    }
    private AdapterCallback callback;

    public void setCallback(AdapterCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapter.DeviceViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.deviceText.setText(device.getName());
        holder.companyText.setText(device.getCompany());
        holder.priceText.setText(Double.toString(device.getPrice()));
        holder.ownerText.setText(device.getUsername());
        Bitmap bitmap = BitmapFactory.decodeByteArray(device.getImage(), 0, device.getImage().length);
        holder.imageView.setImageBitmap(bitmap);

        if (page == HomepageActivity.Page.HOME) {
            holder.actionButton.setText("Rent");
        }
        else if (page == HomepageActivity.Page.MY_DEVICES) {
            holder.actionButton.setText("Delete");
        }
        else holder.actionButton.setText("Return");
        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page == HomepageActivity.Page.MY_DEVICES) {
                    ConfirmationDialog.show(context, "Are you sure you want to delete this Device ?", new ConfirmationDialog.ConfirmationDialogListener() {
                        @Override
                        public void onConfirm() {
                            db.deleteDevice(device.getId());
                            if (callback != null) {
                                callback.onChange();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                else if (page == HomepageActivity.Page.HOME) {
                    ConfirmationDialog.show(context, "Are you sure you want to rent this Device ?", new ConfirmationDialog.ConfirmationDialogListener() {
                        @Override
                        public void onConfirm() {
                            db.rentDevice(device.getId());
                            if (callback != null) {
                                callback.onChange();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
                else {
                    ConfirmationDialog.show(context, "Are you sure you want to return this device ?", new ConfirmationDialog.ConfirmationDialogListener() {
                        @Override
                        public void onConfirm() {
                            db.returnDevice(device.getRentalId());
                            if (callback != null) {
                                callback.onChange();
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceText, companyText, priceText, ownerText;
        public Button actionButton;
        public ImageView imageView;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            deviceText = itemView.findViewById(R.id.device_text);
            companyText = itemView.findViewById(R.id.company_text);
            priceText = itemView.findViewById(R.id.price_text);
            ownerText = itemView.findViewById(R.id.owner_text);
            imageView = itemView.findViewById(R.id.image_view);
            actionButton = itemView.findViewById(R.id.action_button);

        }
    }
}
