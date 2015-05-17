package com.findmycoach.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.findmycoach.app.R;
import com.findmycoach.app.adapter.CardDetailAdapter;
import com.google.android.gms.games.PlayerEntity;

import org.json.JSONArray;

/**
 * Created by ved on 23/3/15.
 */
public class SelectCardForPayment extends Activity implements ImageButton.OnClickListener{
    ImageButton ib_add_card;
    ListView lv_card_details;
    ProgressDialog progressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card_details);
        context=SelectCardForPayment.this;
        progressDialog=new ProgressDialog(SelectCardForPayment.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        initialise();
        applyActionbarProperties();

        lv_card_details.setAdapter(new CardDetailAdapter(context,new JSONArray(),progressDialog));

    }

    private void applyActionbarProperties() {
//        ActionBar actionBar = getActionBar();
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setTitle(getResources().getString(R.string.add_new_slot));
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialise() {
        ib_add_card= (ImageButton) findViewById(R.id.ib_add_new_card);
        ib_add_card.setOnClickListener(this);
        lv_card_details= (ListView) findViewById(R.id.lv_card_details);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_add_new_card:
                Intent intent=new Intent(SelectCardForPayment.this,PaymentDetailsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
