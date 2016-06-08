package com.rishabh.imaniac;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 01-06-2016.
 */
public class TransactionAdapter extends BaseAdapter {
    ArrayList<Transaction> transactions;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;
    LayoutInflater inflater;
    private static String url="https://jsonblob.com/api/570de811e4b01190df5dafec";
    Activity activity;
    public TransactionAdapter(Activity activity, ArrayList transactions){
        super();
        this.transactions =new ArrayList<>();
        this.activity=activity;
        this.transactions =transactions;
    }
    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.list_item_row, null);

        TextView description = (TextView) view.findViewById(R.id.description);
        TextView id = (TextView) view.findViewById(R.id.id);
        TextView category=(TextView)view.findViewById(R.id.category);
        TextView time=(TextView)view.findViewById(R.id.time);
        final TextView amount=(TextView)view.findViewById(R.id.amount);
        TextView state = (TextView) view.findViewById(R.id.state);
        ImageButton edit=(ImageButton)view.findViewById(R.id.edit);

        final Transaction transaction = transactions.get(i);
        description.setText(transaction.getDescription());
        category.setText(transaction.getCategory());
        time.setText(transaction.getTime());
        amount.setText(Integer.toString(transaction.getAmount()));
        id.setText("id - "+transaction.getId());
        state.setText("state - "+transaction.getState());
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(activity);
                dialog.setTitle("Change state");
                dialog.setContentView(R.layout.change_state_dialog);
                dialog.setCancelable(true);
                dialog.show();

                System.out.println(transaction.getId());
                Button ok=(Button)dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        RadioGroup rGroup = (RadioGroup)dialog.findViewById(R.id.states);

                        RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());
                        if(checkedRadioButton!=null){
                            transaction.setState(checkedRadioButton.getText().toString());
                            notifyDataSetChanged();
                            progressDialog=new ProgressDialog(activity);
                            progressDialog.setTitle("Please wait...");
                            progressDialog.setMessage("Changing state of transaction...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            sendTransactions();

                        }
                        else{
                            Toast.makeText(activity,"State was not changed.",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });
        return view;
    }
    void sendTransactions(){
        requestQueue = Volley.newRequestQueue(activity);

        final JSONObject jsonObject=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        int i;
        try{
            for(i=0;i<transactions.size();i++){
                Transaction transaction=transactions.get(i);
                JSONObject jsonObject1=new JSONObject();
                jsonObject1.put("id",transaction.getId());
                jsonObject1.put("category",transaction.getCategory());
                jsonObject1.put("description",transaction.getDescription());
                jsonObject1.put("time",transaction.getTime());
                jsonObject1.put("state",transaction.getState());
                jsonObject1.put("amount",transaction.getAmount());
                jsonArray.put(jsonObject1);
            }

            jsonObject.put("expenses",jsonArray);
            System.out.println(jsonObject.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.PUT, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("its working");
                        Toast.makeText(activity,"State changed successfully.",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(activity,"Error in loading all transactions.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());
                        progressDialog.dismiss();

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {

                try {
                    Log.i("json", jsonObject.toString());
                    return jsonObject.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };
        requestQueue.add(jor);
    }

}
