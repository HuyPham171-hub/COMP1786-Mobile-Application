package com.example.unitconverter;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.Button;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText inputValue;
    Spinner fromUnit, toUnit;
    TextView resultValue;
    Button convertButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputValue = findViewById(R.id.inputValue);
        fromUnit = findViewById(R.id.fromUnit);
        toUnit = findViewById(R.id.toUnit);
        resultValue = findViewById(R.id.resultValue);
        convertButton = findViewById(R.id.convertButton);

        convertButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                convert();
            }
        });
    }

    private void convert(){
        String input = inputValue.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            resultValue.setText("0");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            resultValue.setText("0");
            return;
        }

        String from = fromUnit.getSelectedItem().toString();
        String to = toUnit.getSelectedItem().toString();
        double result = convertLength(value, from, to);

        resultValue.setText(String.format("%.4f", result));
    }

    private double convertLength(double value, String from, String to) {
        double inMetres;
        switch (from) {
            case "Millimetre": inMetres = value / 1000; break;
            case "Mile": inMetres = value * 1609.344; break;
            case "Foot": inMetres = value * 0.3048; break;
            case "Metre":
            default: inMetres = value;
        }

        switch (to) {
            case "Millimetre": return inMetres * 1000;
            case "Mile": return inMetres / 1609.344;
            case "Foot": return inMetres / 0.3048;
            case "Metre":
            default: return inMetres;
        }
    }
}