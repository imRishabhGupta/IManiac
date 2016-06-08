package com.rishabh.imaniac;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    ListView lv;
    private static String url="https://jsonblob.com/api/570de811e4b01190df5dafec";
    ArrayList<Transaction> items;
    TransactionAdapter transactionAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        items=new ArrayList<>();
        lv=(ListView)findViewById(R.id.list);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setMessage("Loading all posts...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        getTransactions();
        transactionAdapter=new TransactionAdapter(this,items);
        lv.setAdapter(transactionAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.refresh){
            items.clear();
            progressDialog.show();
            getTransactions();
        }
        return true;
    }

    void getTransactions(){
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray ja = response.getJSONArray("expenses");
                            System.out.println(ja.length());
                            for(int i=0; i < ja.length(); i++){
                                JSONObject jsonObject = ja.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String description = jsonObject.getString("description");
                                String category=jsonObject.getString("category");
                                String state=jsonObject.getString("state");
                                String time=jsonObject.getString("time");
                                int amount=jsonObject.getInt("amount");
                                Transaction transaction=new Transaction();
                                transaction.setAmount(amount);
                                transaction.setCategory(category);
                                transaction.setId(id);
                                transaction.setDescription(description);
                                transaction.setState(state);
                                transaction.setTime(time);
                                items.add(transaction);
                                System.out.println(id+"  "+state);
                            }
                            transactionAdapter.notifyDataSetChanged();
                            progressDialog.dismiss();

                        }catch(JSONException e){e.printStackTrace();}
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Error in loading all transactions.",Toast.LENGTH_LONG).show();
                        Log.e("Volley",error.toString());

                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        requestQueue.add(jor);
    }




}
