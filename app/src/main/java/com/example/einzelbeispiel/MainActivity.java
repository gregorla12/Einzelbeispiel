package com.example.einzelbeispiel;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    TextView answer;
    Button send;
    EditText matNo;
    private DataOutputStream toServer;
    private BufferedReader fromServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answer = findViewById(R.id.answer);
        matNo = findViewById(R.id.editTextNumber);
        send = findViewById(R.id.send);
        send.setOnClickListener(v -> new Thread(new SendMessageThread()).start());
    }

    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket("se2-isys.aau.at", 53212);//always open new socket as server constantly closes connection
                //Socket socket = socket = new Socket("192.168.0.101",1234);//local server used for testing
                fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                toServer = new DataOutputStream(socket.getOutputStream());
                String message = matNo.getText().toString();
                runOnUiThread(() -> answer.append("\nclient: " + message));
                toServer.writeBytes(message + "\n");
                String result = "\nserver: " + fromServer.readLine();
                runOnUiThread(() -> answer.append(result));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> answer.append("failed somehow..."));
            }
        }
    }

}
