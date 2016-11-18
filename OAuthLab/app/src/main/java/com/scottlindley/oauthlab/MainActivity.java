package com.scottlindley.oauthlab;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String CONSUMER_KEY = "AWCimXwyBSLKABzIikoalhsSo";
    public static final String CONSUMER_SECRET = "W5kVTHGQmlFbSWWqP3r9N7OLlq5rSlItegmGwoe11yX0BBmEPg";


    private List<Tweet> mTweets;
    private RecyclerView mRecyclerView;
    private EditText mEditQuery;
    private Button mSearchButton;
    private String mEncodedCredentials;
    private String mBearerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            String credentials = CONSUMER_KEY + ":" + CONSUMER_SECRET;
            mEncodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
//                    Base64.encodeBase64String(credentials.getBytes());
            obtainBearerToken();
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show();
        }
        mTweets = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(new RecyclerAdapter(mTweets));

        mEditQuery = (EditText) findViewById(R.id.edit_query);
        mSearchButton = (Button) findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info != null && info.isConnected()) {
                    performUserSearch(mEditQuery.getText().toString());
                }
            }
        });

    }

    private void obtainBearerToken() {
        OkHttpClient client = new OkHttpClient();
        Headers headers = new Headers.Builder()
                .add("Authorization", "Basic " + mEncodedCredentials)
                .add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .build();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();
        Request request = new Request.Builder()
                .url("https://api.twitter.com/oauth2/token")
                .headers(headers)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject responseObject = new JSONObject(response.body().string());
                    mBearerToken = responseObject.getString("access_token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void performUserSearch(String query){
        query = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name="+query;
        OkHttpClient client = new OkHttpClient();
        Headers headers = new Headers.Builder()
                .add("Authorization", "Bearer "+mBearerToken)
                .build();
        Request request = new Request.Builder()
                .url(query)
                .headers(headers)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code: "+response);
                }
                try {
                    mTweets.clear();
                    JSONArray rootArray = new JSONArray(response.body().string());
                    for(int i=0; i<rootArray.length(); i++) {
                        JSONObject object = rootArray.getJSONObject(i);
                        String data = object.toString();
                        mTweets.add(new Gson().fromJson(data, Tweet.class));
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
