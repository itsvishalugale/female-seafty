package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {
    private List<HospitalModel> hospitalList;
    private Context context;

    public HospitalAdapter(List<HospitalModel> hospitalList, Context context) {
        this.hospitalList = hospitalList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hospital_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HospitalModel hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getName());
        holder.hospitalAddress.setText("Address: " + hospital.getAddress());
        holder.hospitalPincode.setText("Pincode: " + hospital.getPincode());
        holder.hospitalPhone.setText("Phone: " + hospital.getPhone());

        holder.btnNavigate.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + hospital.getLat() + "," + hospital.getLon() + "?q=" + hospital.getName());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName, hospitalAddress, hospitalPincode, hospitalPhone;
        Button btnNavigate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalAddress = itemView.findViewById(R.id.hospitalAddress);
            hospitalPincode = itemView.findViewById(R.id.hospitalPincode);
            hospitalPhone = itemView.findViewById(R.id.hospitalPhone);
            btnNavigate = itemView.findViewById(R.id.btnNavigate);
 }
}
}
