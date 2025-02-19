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

public class PoliceStationAdapter extends RecyclerView.Adapter<PoliceStationAdapter.ViewHolder> {

    private List<PoliceStationModel> policeStationList;
    private Context context;

    public PoliceStationAdapter(List<PoliceStationModel> policeStationList, Context context) {
        this.policeStationList = policeStationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.police_station_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PoliceStationModel policeStation = policeStationList.get(position);
        holder.policeStationName.setText(policeStation.getName());
        holder.policeStationAddress.setText(policeStation.getAddress());
       holder.policeStationPincode.setText("Pincode: " + policeStation.getPincode());
        holder.policeStationPhone.setText("Phone: " + policeStation.getPhone());

        holder.btnNavigatePolice.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + policeStation.getLatitude() + "," + policeStation.getLongitude() + "?q=" + policeStation.getName());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Open in Google Maps
            context.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return policeStationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView policeStationName, policeStationAddress, policeStationPincode, policeStationPhone;
        Button btnNavigatePolice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            policeStationName = itemView.findViewById(R.id.policeStationName);
            policeStationAddress = itemView.findViewById(R.id.policeStationAddress);
            policeStationPincode = itemView.findViewById(R.id.policeStationPincode);
            policeStationPhone = itemView.findViewById(R.id.policeStationPhone);
            btnNavigatePolice = itemView.findViewById(R.id.btnNavigatePolice);
 }
}
}
