package com.findmycoach.mentor.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.fmc.mentor.findmycoach.R;

public class EditProfileActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    int year = 1990, month = 1, day = 1;
    private TextView dateOfBirthInput;
    private AutoCompleteTextView countryInput;
    private AutoCompleteTextView stateInput;
    private AutoCompleteTextView cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialize();
        applyActionbarProperties();
    }

    private void applyActionbarProperties() {
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        dateOfBirthInput = (TextView) findViewById(R.id.input_date_of_birth);
        countryInput = (AutoCompleteTextView) findViewById(R.id.input_country);
        stateInput = (AutoCompleteTextView) findViewById(R.id.input_state);
        cityInput = (AutoCompleteTextView) findViewById(R.id.input_city);

        applyAction();
    }

    private void applyAction() {
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.countries));
        countryInput.setAdapter(countryAdapter);
        stateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = countryInput.getText().toString().toLowerCase();
                country = country.replaceAll(" ", "_");
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, getResources().getStringArray(getResources().getIdentifier(country, "array", getPackageName())));
                getResources().getIdentifier(country, "array", getPackageName());
                stateInput.setAdapter(stateAdapter);
            }
        });

        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String state = stateInput.getText().toString().toLowerCase();
                state = state.replaceAll(" ", "_");
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, getResources().getStringArray(getResources().getIdentifier(state, "array", getPackageName())));
                cityInput.setAdapter(stateAdapter);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, day);
            datePickerDialog.setTitle("Date of Birth");
            return datePickerDialog;
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        dateOfBirthInput.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        showDate(year, monthOfYear + 1, dayOfMonth);

    }
}
