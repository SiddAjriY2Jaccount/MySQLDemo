package com.example.bright.mysqldemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText password,userName;
    Button login,newUser;
    ProgressBar progressBar;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (EditText)findViewById(R.id.userName);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        newUser = (Button)findViewById(R.id.newUser);
        textView = (TextView)findViewById(R.id.status);

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterUser.class));
            }
        });
        //send the data to the Servlet
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = userName.getText().toString();
                String pwd = password.getText().toString();
                new MyTask().execute(uName, pwd);
            }
        });
    }
    class MyTask extends AsyncTask<String, Void, String>{
        StringBuffer sb = null;
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            URL url = null;
            boolean first = true;
            StringBuffer stringBuffer = new StringBuffer();
            try {
                 url = new URL("http://ur_ip_address:8080/WebAppBright/NewServlet?");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.setDoOutput(true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", params[0]);
                jsonObject.put("password", params[1]);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (first){
                        first = false;
                    }else{
                        stringBuffer.append("&");
                    }
                    stringBuffer.append(URLEncoder.encode(key, "UTF-8"));
                    stringBuffer.append("=");
                    stringBuffer.append(URLEncoder.encode(value.toString(), "UTF-8"));
                }
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(outputStream));
                bufferedWriter.write(stringBuffer.toString());
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                //get the response from the Servlet
                int responseCode=httpURLConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    sb = new StringBuffer("");
                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    httpURLConnection.getInputStream()));

                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

    }
}
