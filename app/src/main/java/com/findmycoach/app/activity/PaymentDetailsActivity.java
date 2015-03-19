package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.DatePickerFragment;

/**
 * Created by prem on 9/3/15.
 */
public class PaymentDetailsActivity extends Activity implements View.OnClickListener {

    private EditText inputCardNumber, inputCardName, inputCardCVV;
    public static EditText inputCardExpiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        initialize();
        applyActionbarProperties();
    }

    private void initialize() {
        inputCardNumber = (EditText) findViewById(R.id.editTextCard);
        inputCardName = (EditText) findViewById(R.id.editTextName);
        inputCardExpiry = (EditText) findViewById(R.id.editTextDate);
        inputCardCVV = (EditText) findViewById(R.id.editTextCVV);
        findViewById(R.id.buttonSkip).setOnClickListener(this);
        findViewById(R.id.buttonSave).setOnClickListener(this);
        inputCardExpiry.setOnClickListener(this);
    }

    private void applyActionbarProperties() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null)
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonSkip:
                finish();
                break;
            case R.id.buttonSave:
                if(validateData(inputCardNumber.getText().toString(), inputCardName.getText().toString(),
                        inputCardExpiry.getText().toString(), inputCardCVV.getText().toString())) {
                    Toast.makeText(this, "Card details will be saved", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case R.id.editTextDate:
                showDateTimeDialog();
                break;
        }
    }

    private void showDateTimeDialog() {
        DialogFragment datePickerDialog = new DatePickerFragment();
        datePickerDialog.show(getFragmentManager(), "timePicker");
    }

    private boolean validateData(String cardNumber, String cardName, String cardExpiry, String cardCVV) {
        if (cardNumber.equals("")) {
            showErrorMessage(inputCardNumber, getResources().getString(R.string.error_field_required));
            return false;
        }else if (cardNumber.length() < 16) {
            showErrorMessage(inputCardNumber, getResources().getString(R.string.error_invalid_details));
            return false;
        }

        if (cardName.equals("")) {
            showErrorMessage(inputCardName, getResources().getString(R.string.error_field_required));
            return false;
        }else{
            for (int i = 0; i < cardName.length()-1; i++) {
                if (!Character.isLetter(cardName.charAt(i))) {
                    showErrorMessage(inputCardName, getResources().getString(R.string.error_not_a_name));
                    return false;
                }
            }
        }

        if (cardExpiry.equals("")) {
            showErrorMessage(inputCardExpiry, getResources().getString(R.string.error_field_required));
            return false;
        }

        if (cardCVV.equals("")) {
            showErrorMessage(inputCardCVV, getResources().getString(R.string.error_field_required));
            return false;
        }

            return true;
    }

    private void showErrorMessage(final TextView view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }
}
