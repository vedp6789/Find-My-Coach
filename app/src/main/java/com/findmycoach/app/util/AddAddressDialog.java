package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.findmycoach.app.R;
import com.findmycoach.app.beans.student.Address;
import com.findmycoach.app.beans.student.ChildDetails;
import com.findmycoach.app.beans.suggestion.Prediction;
import com.findmycoach.app.beans.suggestion.Suggestion;
import com.findmycoach.app.views.ChizzleAutoCompleteTextView;
import com.findmycoach.app.views.ChizzleEditText;
import com.findmycoach.app.views.ChizzleTextView;
import com.findmycoach.app.views.DobPicker;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by abhi7 on 27/07/15.
 */
public class AddAddressDialog implements  Callback {

    private Context context;
    private Dialog dialog;
    private ChizzleEditText addressLine1,zip;
    private ChizzleAutoCompleteTextView locality;
    private Button doneButton;
    private Button cancelButton;
    private AddressAddedListener mAddressAddedListener;

    public AddAddressDialog(Context context) {
        this.context = context;


    }



    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_address_dialog);
        addressLine1=(ChizzleEditText)dialog.findViewById(R.id.addressLine1);
        doneButton=(Button)dialog.findViewById(R.id.done);
        zip=(ChizzleEditText)dialog.findViewById(R.id.zip);
        cancelButton=(Button)dialog.findViewById(R.id.cancel);
        locality=(ChizzleAutoCompleteTextView)dialog.findViewById(R.id.locality);

        locality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = locality.getText().toString().trim();
                if (input.length() >= 2) {
                    getAutoSuggestions(input);
         }
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address address=new Address();
                address.setAddressLine1(addressLine1.getText().toString());
                address.setLocality(locality.getText().toString());
                address.setZip(zip.getText().toString());
                onChildDetailsAdded(address);
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();


    }

    private void getAutoSuggestions(String input) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("input", input);
        requestParams.add("key",context.getResources().getString(R.string.google_location_api_key));
        NetworkClient.autoComplete(context, requestParams, AddAddressDialog.this, 32);
    }


    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        if (object instanceof Suggestion) {
            Suggestion suggestion = (Suggestion) object;
            updateAutoSuggestion(suggestion);
        }
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {

    }

    private void updateAutoSuggestion(Suggestion suggestion) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            List<Prediction> suggestions = suggestion.getPredictions();
            for (int index = 0; index < suggestions.size(); index++) {
                list.add(suggestions.get(index).getDescription());
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(context, R.layout.textview, list);
            locality.setAdapter(arrayAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setAddressAddedListener(
            AddressAddedListener childDetailsAddedListener) {
        mAddressAddedListener = childDetailsAddedListener;
    }

    public void onChildDetailsAdded (Address address) {
        mAddressAddedListener.onAddressAdded(address);
    }

    public interface AddressAddedListener {
        public void onAddressAdded(Address address);
    }


}
