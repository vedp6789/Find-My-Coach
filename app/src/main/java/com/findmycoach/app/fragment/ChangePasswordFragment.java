package com.findmycoach.app.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.findmycoach.app.R;
import com.findmycoach.app.util.Callback;
import com.findmycoach.app.util.NetworkClient;
import com.findmycoach.app.util.StorageHelper;
import com.loopj.android.http.RequestParams;

/**
 * Created by ved on 17/3/15.
 */
public class ChangePasswordFragment extends DialogFragment implements View.OnClickListener,Callback{
    EditText et_new_password,et_confirm_new_password;
    Button b_confirm;
    private String new_password;
    private String confirm_new_password;
    ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_change_password,container,false);
        et_new_password= (EditText) view.findViewById(R.id.et_new_password);

        et_confirm_new_password= (EditText) view.findViewById(R.id.et_confirm_password);

        b_confirm= (Button) view.findViewById(R.id.b_confirm_new_password);
        b_confirm.setOnClickListener(this);

        Dialog dialog = getDialog();
        dialog.setTitle(getString(R.string.change_password));
        dialog.setCanceledOnTouchOutside(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.b_confirm_new_password:
                if(validate()){
                    Toast.makeText(getActivity(),"Network Communication will start",Toast.LENGTH_SHORT).show();

                    RequestParams requestParams=new RequestParams();
                    requestParams.add("id", StorageHelper.getUserDetails(getActivity(),"user_id"));
                    requestParams.add("password",confirm_new_password);
                    progressDialog.show();
                    NetworkClient.resetPassword(getActivity(),requestParams,StorageHelper.getUserDetails(getActivity(), "auth_token"),this,44);

                }
                break;
        }
    }

    boolean validate(){
        new_password=et_new_password.getText().toString();
        confirm_new_password=et_confirm_new_password.getText().toString();
        if(new_password.equals("")){

            if(confirm_new_password.equals("")){
                showErrorMessage2(et_new_password,et_confirm_new_password,getResources().getString(R.string.error_field_required));
                return false;
            }else{
                showErrorMessage(et_new_password,getResources().getString(R.string.error_field_required));
                return false;
            }
        }

        if(!new_password.equals(confirm_new_password)){
            showErrorMessage(et_confirm_new_password,getResources().getString(R.string.error_field_not_match));
            return false;
        }


        return true;




    }

    private void showErrorMessage(final EditText view, String string) {
        view.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }

    private void showErrorMessage2(final EditText view,final EditText editText, String string) {
        view.setError(string);
        editText.setError(string);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setError(null);
            }
        }, 3500);
    }

    @Override
    public void successOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        dismiss();
        Toast.makeText(getActivity(),(String )object,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failureOperation(Object object, int statusCode, int calledApiValue) {
        progressDialog.dismiss();
        dismiss();
        Toast.makeText(getActivity(),(String )object,Toast.LENGTH_SHORT).show();
    }
}
