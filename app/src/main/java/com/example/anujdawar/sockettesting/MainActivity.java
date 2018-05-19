package com.example.anujdawar.sockettesting;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    Socket s = null;
    DataInputStream dataIn;
    DataOutputStream dataOut;
    Button connectButton, disconnectButton;
    Thread test;
    byte connectCounter = 0;
    boolean flag = false;

    Handler h1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = "";
            data = msg.obj.toString();
            if(data.equals("c"))
                connectCounter++;
            if(!flag)
            {
                flag = true;
                checkCounter();
            }
        }
    };

    Handler Hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, "disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonListener();
    }

    private void setButtonListener()
    {
        connectButton = (Button) findViewById(R.id.button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketTest();
            }
        });
        disconnectButton = (Button) findViewById(R.id.button2);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnectSocket();
            }
        });
    }

    private void disconnectSocket()
    {
        click("00000");
    }

    private void checkCounter()
    {
        Thread counterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (connectCounter != 0)
                        connectCounter = 0;
                    else
                        Hand.sendEmptyMessage(0);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        counterThread.start();
    }

    private void socketTest()
    {
        test = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String ip = "192.168.43.45";
                    int port = 80;
                    s = new Socket(ip, port);
                    dataIn = new DataInputStream(s.getInputStream());
                    dataOut = new DataOutputStream(s.getOutputStream());
                    ConnectButton();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        test.start();
    }

    public void click(final String s)
    {
        Thread sendTo = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    dataOut.writeBytes(s);
                    dataOut.flush();
                    dataOut.flush();
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        sendTo.start();
    }

    public void ConnectButton()
    {
        Thread connectThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    while(true)
                    {
                        byte temp = dataIn.readByte();
                        char convert = (char) temp;
                        Message msg = Message.obtain();
                        msg.obj = convert;
                        h1.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connectThread.start();
    }
}