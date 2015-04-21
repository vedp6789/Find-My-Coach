package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
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
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by prem on 9/3/15.
 */
public class PaymentDetailsActivity extends Activity implements View.OnClickListener {

    private EditText inputCardNumber, inputCardName, inputCardCVV;
    public static EditText inputCardExpiry;
    private final int MY_SCAN_REQUEST_CODE = 10001;
    private final int REQUEST_CODE_FUTURE_PAYMENT = 11111;
    private final String TAG = "FMC";

    private static PayPalConfiguration config = new PayPalConfiguration()

            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)

            .clientId("Aaqjb3uZDZz7GZHul2SAi52eXjbjKmnD9m1TijI8Y-UjeEZBmJ-7cat1KAEW8feE6o4TV82nMhl-kkaG")

                    // Minimally, you will need to set three merchant information properties.
                    // These should be the same values that you provided to PayPal when you registered your app.
            .merchantName("Chizzle")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        initialize();
        applyActionbarProperties();


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onFuturePaymentPressed() {
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
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
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

        }
        else if(id == R.id.action_add_paypal){
            // Call paypal service to get customer consent
            onFuturePaymentPressed();
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
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            Log.d(TAG, resultDisplayStr);
        }

        // Received customer consent from paypal
        else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FUTURE_PAYMENT)  {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
            if (auth != null) {
                String authorization_code = auth.getAuthorizationCode();
                Log.d(TAG, authorization_code);
                Toast.makeText(this, authorization_code, Toast.LENGTH_LONG).show();

            }
        }

        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("FuturePaymentExample", "The user canceled.");
        } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("FuturePaymentExample",
                    "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
        }
    }
}
