package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.DatePickerFragment;
import com.findmycoach.app.util.Callback;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by prem on 9/3/15.
 */
public class PaymentDetailsActivity extends Activity implements View.OnClickListener, Callback {

    private EditText inputCardNumber, inputCardName, inputCardCVV;
    public static EditText inputCardExpiry;
    private final int MY_SCAN_REQUEST_CODE = 10001;
    private final String TAG = "FMC";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        initialize();
        applyActionbarProperties();
    }

    private void initialize() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
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
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if(id == R.id.action_scan_card){
            Intent scanIntent = new Intent(this, CardIOActivity.class);

            // customize these values to suit your needs.
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);

            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

        }
        return true;
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
                    Toast.makeText(this, getResources().getString(R.string.cards_detail_will_saved), Toast.LENGTH_LONG).show();
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

        cardName = cardName.replaceAll(" ", "");

        if (cardNumber.equals("")) {
            showErrorMessage(inputCardNumber, getResources().getString(R.string.error_field_required));
            return false;
        }else if (cardNumber.length() < 15) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received card info from card.io
        if (requestCode == MY_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }

                inputCardNumber.setText(scanResult.getRedactedCardNumber());
                inputCardExpiry.setText(DatePickerFragment.getMonth(scanResult.expiryMonth) + " - " + scanResult.expiryYear);
                inputCardCVV.setText(scanResult.cvv);

            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            Log.d(TAG, resultDisplayStr);
        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
//        progressDialog.dismiss();
        // Get details of last transaction done
        if(calledApiValue == 48){
            //TODO with the transaction details received with status
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
//        progressDialog.dismiss();
        // Get details of last transaction done
        if(calledApiValue == 48){
            //TODO with the transaction details received with status
        }
    }
}
