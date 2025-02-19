package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final ArrayList<ContactModel> arrContact;
    private final ArrayList<ContactModel> arrContactFull;
    private final DatabaseReference databaseReference;

    public RecyclerContactAdapter(Context context, ArrayList<ContactModel> arrContact, DatabaseReference databaseReference) {
        this.context = context;
        this.arrContact = arrContact;
        this.arrContactFull = new ArrayList<>(arrContact);
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactModel model = arrContact.get(position);
        holder.txtName.setText(model.getName());
        holder.txtNumber.setText(model.getNumber());

        holder.btnCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + model.getNumber()));
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        databaseReference.child(model.getId()).removeValue();
                        arrContact.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return arrContact.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageButton btnCall, btnDelete; // FIXED: Changed from Button to ImageButton

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ContactModel> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(arrContactFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ContactModel item : arrContactFull) {
                        if (item.getName().toLowerCase().contains(filterPattern) ||
                                item.getNumber().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrContact.clear();
                arrContact.addAll((List<ContactModel>) results.values);
                notifyDataSetChanged();
            }
  };
}
}
