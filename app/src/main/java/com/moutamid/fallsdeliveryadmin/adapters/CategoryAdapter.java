package com.moutamid.fallsdeliveryadmin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.moutamid.fallsdeliveryadmin.R;
import com.moutamid.fallsdeliveryadmin.models.CategoryModel;
import com.moutamid.fallsdeliveryadmin.utilis.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> implements Filterable {
    Context context;
    ArrayList<CategoryModel> list;
    ArrayList<CategoryModel> listAll;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CategoryModel> filterList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filterList.addAll(listAll);
            } else {
                for (CategoryModel listModel : listAll){
                    if (listModel.name.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterList.add(listModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends CategoryModel>) results.values);
            notifyDataSetChanged();
        }
    };


    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryVH(LayoutInflater.from(context).inflate(R.layout.category_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        CategoryModel model = list.get(holder.getAdapterPosition());
        holder.name.setText(model.name);

        holder.itemView.setOnLongClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.add_topic);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();

            TextInputLayout topic = dialog.findViewById(R.id.name);
            Button complete = dialog.findViewById(R.id.complete);
            topic.getEditText().setText(model.name);
            complete.setOnClickListener(v1 -> {
                String topicName = topic.getEditText().getText().toString();
                if (!topicName.isEmpty()) {
                    dialog.dismiss();
                    model.name = topicName;
                    Constants.databaseReference().child(model.type).child(model.uid).setValue(model)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "Category Updated Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                } else {
                    topic.setErrorEnabled(true);
                    topic.setError("Category name is empty");
                }
            });

            return false;
        });

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setCancelable(true)
                    .setTitle("Delete " + model.name)
                    .setMessage("Are you sure you want to delete this category.\nThe content of this category will also be deleted.")
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        dialog.dismiss();
                        Constants.databaseReference().child(model.type).child(model.uid).removeValue()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Category Deleted Successfully", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }))
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {
        TextView name;
        MaterialCardView delete;
        public CategoryVH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
