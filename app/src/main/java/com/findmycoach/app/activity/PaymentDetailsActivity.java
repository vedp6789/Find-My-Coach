package com.findmycoach.app.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.fragment.DatePickerFragment;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by prem on 9/3/15.
 */
public class PaymentDetailsActivity extends Activity implements View.OnClickListener, Callback {

    private EditText inputCardNumber, inputCardName, inputCardCVV;
    public static EditText inputCardExpiry;
    private final int MY_SCAN_REQUEST_CODE = 10001;
    private final int REQUEST_CODE_FUTURE_PAYMENT = 11111;
    private final String TAG = "FMC";
    private boolean isConsentPresent;
    private ProgressDialog progressDialog;

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

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    public void onFuturePaymentPressed() {
        Intent intent = new Intent(this, PayPalFuturePaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onFuturePaymentPurchasePressed() {
        // Get the Client Metadata ID from the PayPal SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);
        String amount = "11111";
        String mentorId = "xxx";
        Log.d(TAG, "User's MetaData ID : " + metadataId);
        Toast.makeText(this, "MetaData : " + metadataId, Toast.LENGTH_LONG).show();

//        progressDialog.show();
        RequestParams requestParams = new RequestParams();
        requestParams.add("id", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
        requestParams.add("mentor_id", mentorId);
        requestParams.add("user_group", String.valueOf(DashboardActivity.dashboardActivity.user_group));
        requestParams.add("meta_data", metadataId);
        requestParams.add("amount", amount);
//        NetworkClient.payMentor(this, requestParams, this, 48);
    }

    private void initialize() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isConsentPresent = preferences.getBoolean(getResources().getString(R.string.customer_consent), false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        inputCardNumber = (EditText) findViewById(R.id.editTextCard);
        inputCardName = (EditText) findViewById(R.id.editTextName);
        inputCardExpiry = (EditText) findViewById(R.id.editTextDate);
        inputCardCVV = (EditText) findViewById(R.id.editTextCVV);
        findViewById(R.id.buttonSkip).setOnClickListener(this);
        findViewById(R.id.buttonSave).setOnClickListener(this);
        inputCardExpiry.setOnClickListener(this);

        try {
            inputCardName.setText(StorageHelper.getUserDetails(this, "user_full_name"));
        } catch (Exception e) {
            inputCardName.setText("");
        }

        findViewById(R.id.scanCard).setOnClickListener(this);
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
        if (DashboardActivity.dashboardActivity == null)
            startActivity(new Intent(this, DashboardActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonSkip:
                finish();
                break;
            case R.id.buttonSave:
                if (validateData(inputCardNumber.getText().toString(), inputCardName.getText().toString(),
                        inputCardExpiry.getText().toString(), inputCardCVV.getText().toString())) {
                    Toast.makeText(this, getResources().getString(R.string.cards_detail_will_saved), Toast.LENGTH_LONG).show();
//                progressDialog.show();
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("id", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
                    requestParams.add("email", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_email)));
                    requestParams.add("user_group", String.valueOf(DashboardActivity.dashboardActivity.user_group));
                    requestParams.add("name_on_card", inputCardName.getText().toString().trim());
                    requestParams.add("card_number", inputCardNumber.getText().toString().trim());
                    requestParams.add("card_expiry", inputCardExpiry.getText().toString().trim());
                    requestParams.add("card_cvv", inputCardCVV.getText().toString().trim());
//                NetworkClient.saveCardDetails(this, requestParams, this, 53);
                    finish();
                }
                break;
            case R.id.editTextDate:
                showDateTimeDialog();
                break;
            case R.id.scanCard:
                Intent scanIntent = new Intent(PaymentDetailsActivity.this, CardIOActivity.class);

                Log.e(TAG, "Scan Clicked");
                // customize these values to suit your needs.
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true);
                // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
                startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
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
        } else if (cardNumber.length() < 15) {
            showErrorMessage(inputCardNumber, getResources().getString(R.string.error_invalid_details));
            return false;
        }

        if (cardName.equals("")) {
            showErrorMessage(inputCardName, getResources().getString(R.string.error_field_required));
            return false;
        } else {
            for (int i = 0; i < cardName.length() - 1; i++) {
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
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            try {
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

                    inputCardNumber.setText(scanResult.cardNumber);
                    inputCardExpiry.setText(DatePickerFragment.getMonth(scanResult.expiryMonth) + " - " + scanResult.expiryYear);
                    inputCardCVV.setText(scanResult.cvv);

                } else {
                    resultDisplayStr = "Scan was canceled.";
                }
                Log.d(TAG, resultDisplayStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Received customer consent from PayPal
        else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            PayPalAuthorization auth = data
                    .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
            if (auth != null) {
                String authorization_code = auth.getAuthorizationCode();
                Log.d(TAG, authorization_code);
                Toast.makeText(this, authorization_code, Toast.LENGTH_LONG).show();

                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(getResources().getString(R.string.customer_consent), true).apply();
                isConsentPresent = true;

                //TODO update menu to pay using paypal

//                progressDialog.show();
                RequestParams requestParams = new RequestParams();
                requestParams.add("id", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_id)));
                requestParams.add("email", StorageHelper.getUserDetails(this, getResources().getString(R.string.user_email)));
                requestParams.add("user_group", String.valueOf(DashboardActivity.dashboardActivity.user_group));
                requestParams.add("paypal_consent", authorization_code);
//                NetworkClient.savePayPalConsent(this, requestParams, this, 47);
            }
        }
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
//        progressDialog.dismiss();

        // Get details of user's saved card and PayPal account
        if (calledApiValue == 47) {
            isConsentPresent = true;
            //TODO update menu to pay using paypal
        }

        // Get details of last transaction done
        else if (calledApiValue == 48) {
            //TODO with the transaction details received with status
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
//        progressDialog.dismiss();
        // Get details of user's saved card and PayPal account
        if (calledApiValue == 47) {
            isConsentPresent = true;
            //TODO update menu to add paypal
        }

        // Get details of last transaction done
        if (calledApiValue == 48) {
            //TODO with the transaction details received with status
        }
    }
}
