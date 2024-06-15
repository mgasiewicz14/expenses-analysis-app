package com.example.expensesanalysisapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.PercentFormatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextAmount, editTextDescription;
    private Button buttonAddTransaction, buttonPickDate, buttonClearDatabase;
    private Spinner spinnerCategory;
    private TextView textViewDateLabel;
    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private TransactionDb transactionDb;
    private TransactionAdapter transactionAdapter;
    private PieChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAmount = findViewById(R.id.editTextAmount);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        buttonClearDatabase = findViewById(R.id.buttonClearDatabase);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        textViewDateLabel = findViewById(R.id.textViewDateLabel);
        chart = findViewById(R.id.pieChart);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTransactions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        List<String> categories = Arrays.asList("Jedzenie", "Rozrywka", "Czynsz");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        transactionDb = new TransactionDb(this);
        transactionAdapter = new TransactionAdapter(this, transactionDb.getAllTransactions(), categories, transactionDb);

        buttonPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
                updatePieChart();
            }
        });

        buttonClearDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDatabase();
            }
        });




        recyclerView.setAdapter(transactionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Transaction> allTransactions = transactionDb.getAllTransactions();
        for (Transaction transaction : allTransactions) {
        }
        setupPieChart(chart);
        updatePieChart();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateLabel();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawEntryLabels(false);
    }

    private void updatePieChart() {
        List<Transaction> allTransactions = transactionDb.getAllTransactions();

        List<PieEntry> entries = new ArrayList<>();
        Map<String, Float> categoryAmountMap = new HashMap<>();


        float totalExpenses = 0.0f;

        for (Transaction transaction : allTransactions) {
            String category = transaction.getCategory();
            float amount = (float) transaction.getAmount();

            if (categoryAmountMap.containsKey(category)) {
                amount += categoryAmountMap.get(category);
            }
            categoryAmountMap.put(category, amount);

            totalExpenses += amount;
        }

        TextView textViewTotalExpenses = findViewById(R.id.textViewTotalExpenses);
        textViewTotalExpenses.setText(String.valueOf(totalExpenses));

        for (Map.Entry<String, Float> entry : categoryAmountMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Kategorie");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);


        chart.setData(data);
        chart.invalidate();
    }
    private void updateDateLabel() {
        textViewDateLabel.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void addTransaction() {
        try {
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            String category = spinnerCategory.getSelectedItem().toString();
            String description = editTextDescription.getText().toString();
            String date = dateFormat.format(selectedDate.getTime());

            transactionDb.addTransaction(new Transaction(amount, category, description, date));
            updateTransactionList();
            editTextAmount.setText("");
            spinnerCategory.setSelection(0);
            editTextDescription.setText("");
            selectedDate = Calendar.getInstance();
            updateDateLabel();
        } catch (NumberFormatException e) {
            e.printStackTrace();

        }
    }

    private void clearDatabase() {
        transactionDb.clearAllTransactions();
        
        updateTransactionList();
        updatePieChart();
    }

    private void updateTransactionList() {
        transactionAdapter.setTransactionList(transactionDb.getAllTransactions());
        transactionAdapter.notifyDataSetChanged();
    }
}