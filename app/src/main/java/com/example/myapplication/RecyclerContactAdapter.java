package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private final ArrayList<ContactModel> arrContact;
    private ArrayList<ContactModel> arrContactFull; // For filtering

    public RecyclerContactAdapter(Context context, ArrayList<ContactModel> arrContact) {
        this.context = context;
        this.arrContact = arrContact;
        this.arrContactFull = new ArrayList<>(arrContact); // Copy original list
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
        holder.imgContact.setImageResource(model.img);
        holder.txtNumber.setText(model.number);
        holder.txtName.setText(model.name);

        holder.llRow.setOnClickListener(view -> openEditDialog(position));

        holder.llRow.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Contact")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        arrContact.remove(position);
                        notifyItemRemoved(position);
                        ((EmergencyContactActivity) context).saveContactsToPreferences();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });
    }

    private void openEditDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_add_contact_dialog);

        EditText edtName = dialog.findViewById(R.id.edtName);
        EditText edtNumber = dialog.findViewById(R.id.edtNumber);
        Button btnAction = dialog.findViewById(R.id.btnAction);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        tvTitle.setText("Update Contact");
        btnAction.setText("Update");

        edtName.setText(arrContact.get(position).name);
        edtNumber.setText(arrContact.get(position).number);

        btnAction.setOnClickListener(view -> {
            String name = edtName.getText().toString();
            String number = edtNumber.getText().toString();

            if (!name.isEmpty() && !number.isEmpty()) {
                arrContact.set(position, new ContactModel(arrContact.get(position).img, name, number));
                notifyItemChanged(position);
                ((EmergencyContactActivity) context).saveContactsToPreferences();
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Please enter a contact name and number", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return arrContact.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtNumber;
        ImageView imgContact;
        LinearLayout llRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtNumber = itemView.findViewById(R.id.txtNumber);
            imgContact = itemView.findViewById(R.id.imgContact);
            llRow = itemView.findViewById(R.id.llRow);
        }
    }

    // Implement filtering
    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    private final Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ContactModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrContactFull); // Show all if no input
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ContactModel contact : arrContactFull) {
                    if (contact.name.toLowerCase().contains(filterPattern) ||
                            contact.number.toLowerCase().contains(filterPattern)) {
                        filteredList.add(contact);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrContact.clear();
            arrContact.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
