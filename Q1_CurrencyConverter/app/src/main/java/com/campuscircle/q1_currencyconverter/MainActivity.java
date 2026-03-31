package com.campuscircle.q1_currencyconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText amountInput;
    TextView resultText;
    Spinner fromSpinner, toSpinner;
    SwitchCompat themeSwitch; // Upgraded to SwitchCompat
    Button convertButton;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("isDark", false);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // This will now work because we added android:id="@+id/main" to the XML
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        amountInput = findViewById(R.id.inputAmount);
        resultText = findViewById(R.id.outputResult);
        fromSpinner = findViewById(R.id.fromCurrencySpinner);
        toSpinner = findViewById(R.id.toCurrencySpinner);
        themeSwitch = findViewById(R.id.themeSwitch);
        convertButton = findViewById(R.id.convertButton);

        themeSwitch.setChecked(isDark);

        String[] currencies = {"USD", "INR", "EUR", "JPY"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("isDark", isChecked).apply();
            recreate();
        });

        // Replaced anonymous inner class with a clean lambda expression
        convertButton.setOnClickListener(v -> doMathAndConvert());
    }

    private void doMathAndConvert() {
        String input = amountInput.getText().toString();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amountToConvert = Double.parseDouble(input);
        String currencyFrom = fromSpinner.getSelectedItem().toString();
        String currencyTo = toSpinner.getSelectedItem().toString();

        double rateFrom = getRateForCurrency(currencyFrom);
        double rateTo = getRateForCurrency(currencyTo);

        double finalResult = (amountToConvert / rateFrom) * rateTo;

        // Added Locale.getDefault() to fix the formatting warning
        resultText.setText(String.format(Locale.getDefault(), "%.2f %s", finalResult, currencyTo));
    }

    private double getRateForCurrency(String currency) {
        switch (currency) {
            case "INR": return 93.62;
            case "EUR": return 0.87;
            case "JPY": return 159.87;
            // Removed the duplicate "USD" case since the default handles it exactly the same
            default: return 1.0;
        }
    }
}