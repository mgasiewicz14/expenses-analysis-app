package com.example.expensesanalysisapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;
    private List<String> categories;
    private TransactionDb transactionDb;

    public TransactionAdapter(Context context, List<Transaction> transactionList, List<String> categories, TransactionDb transactionDb) {
        this.context = context;
        this.transactionList = transactionList;
        this.categories = categories;
        this.transactionDb = transactionDb;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.textViewAmount.setText(String.valueOf(transaction.getAmount()));
        holder.spinnerCategory.setSelection(categories.indexOf(transaction.getCategory()));
        holder.textViewDate.setText(transaction.getDate());
        holder.textViewDescription.setText(transaction.getDescription());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
        notifyDataSetChanged();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAmount, textViewDate, textViewDescription;
        Spinner spinnerCategory;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAmount = itemView.findViewById(R.id.textViewAmount);
            spinnerCategory = itemView.findViewById(R.id.spinnerCategory);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(categoryAdapter);
        }
    }
}
