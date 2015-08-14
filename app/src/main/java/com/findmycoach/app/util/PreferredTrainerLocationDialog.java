package com.findmycoach.app.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.PreferredAddressAdapter;
import com.findmycoach.app.beans.student.Address;

import java.util.ArrayList;

/**
 * Created by abhi7 on 10/08/15.
 */
public class PreferredTrainerLocationDialog {

    private Context context;
    private Dialog dialog;
    private static ArrayList<String> addressList;
    private ListView addressListView;
    private static PreferredAddressAdapter preferredAddressAdapter;
    private Button doneButton;
    private Button cancelButton;
    private PreferredAddressSelectedListener mAddressAddedListener;
    private String previousAddress;
    private int index;


    public PreferredTrainerLocationDialog(Context context, ArrayList<String> addressList, String previousAddress, int index) {
        this.context = context;
        this.addressList = addressList;
        this.previousAddress = previousAddress;
        this.index = index;


    }

    public void showPopUp() {
        dialog = new Dialog(context, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.prefered_trainer_dialog_layout);
        addressListView = (ListView) dialog.findViewById(R.id.preferredAddressList);
        doneButton = (Button) dialog.findViewById(R.id.done);
        cancelButton = (Button) dialog.findViewById(R.id.cancel);
        preferredAddressAdapter = new PreferredAddressAdapter(context, R.layout.preferred_location_dialog_list_item, addressList);
        addressListView.setAdapter(preferredAddressAdapter);
        dialog.setCanceledOnTouchOutside(false);

        if (index == -1) {
            for (int i = 0; i < addressList.size(); i++) {
                if (previousAddress.equalsIgnoreCase(addressList.get(i))) {
                    preferredAddressAdapter.selectedIndex = i;
                }
            }
        } else {
            preferredAddressAdapter.selectedIndex = index;

        }
        preferredAddressAdapter.notifyDataSetChanged();
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferredAddressAdapter.selectedIndex != -1) {
                    onAddressSelected(addressList.get(preferredAddressAdapter.selectedIndex), preferredAddressAdapter.selectedIndex);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Please select one", Toast.LENGTH_SHORT).show();
                }

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


    public static void changeAddresses(ArrayList<String> preferredAddressList) {
        addressList = preferredAddressList;
        preferredAddressAdapter.notifyDataSetChanged();

    }

    public static void changeDeletedAddress(ArrayList<Address> preferredAddressList) {
        if (addressList != null) {
            for (int i = 1; i < addressList.size(); i++) {
                addressList.remove(i);
                i--;
            }
            for (int i = 0; i < preferredAddressList.size(); i++) {
                addressList.add(i + 1, preferredAddressList.get(i).getLocale());
            }
            preferredAddressAdapter.notifyDataSetChanged();
        }


    }

    public void setAddressSelectedListener(
            PreferredAddressSelectedListener addressSelectedListener) {
        mAddressAddedListener = addressSelectedListener;
    }

    public void onAddressSelected(String address, int index) {
        mAddressAddedListener.onAddressSelected(address, index);
    }

    public interface PreferredAddressSelectedListener {
        public void onAddressSelected(String address, int index);
    }


}
