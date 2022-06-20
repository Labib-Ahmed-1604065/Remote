package com.arif.remote;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.util.UUID;

public class FloatingViewService extends Service implements View.OnClickListener {

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;

    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    //private Controlling.ReadInput mReadThread = null;

    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;

    private BluetoothDevice mDevice;

    final static String forward="1";//forward
    final static String backward="2";//backward
    final static String left="3";//left
    final static String right="4";//right
    final static String goUp="5";//goUp
    final static String goDown="6";//goDown
    final static String stop="0";//stop

    private ProgressDialog progressDialog;
    Button btnForward, btnBackward, btnLeft, btnRight, btnGoUp, btnGoDown, btnStop;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //setContentView(R.layout.layout_floating_widget);
        //button id with Button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }
        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //ActivityHelper.initialize(this);
        // mBtnDisconnect = (Button) findViewById(R.id.btnDisconnect);

        btnForward =(Button) mFloatingView.findViewById(R.id.forward);
        btnBackward =(Button) mFloatingView.findViewById(R.id.backward);
        btnLeft =(Button) mFloatingView.findViewById(R.id.left);
        btnRight =(Button) mFloatingView.findViewById(R.id.right);
        btnGoUp =(Button) mFloatingView.findViewById(R.id.goUp);
        btnGoDown =(Button) mFloatingView.findViewById(R.id.goDown);
        btnStop =(Button) mFloatingView.findViewById(R.id.stop);


        //https://github.com/ahmedwahba/cordova-plugin-drawoverapps/issues/11
        //setting the layout parameters
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            //getting windows services and adding the floating view to it
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);


            //getting the collapsed and expanded view from the floating view
            collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
            expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

            //adding click listener to close button and expanded view
            mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
            expandedView.setOnClickListener(this);

            //adding an touchlistener to make drag movement of the floating widget
            mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:
                            //when the drag is ended switching the state of the widget
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });}
        else{
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            //getting windows services and adding the floating view to it
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);


            //getting the collapsed and expanded view from the floating view
            collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
            expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

            //adding click listener to close button and expanded view
            mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
            expandedView.setOnClickListener(this);

            //adding an touchlistener to make drag movement of the floating widget
            mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_UP:
                            //when the drag is ended switching the state of the widget
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });}

        /////////////////////////////////////////////////////////////////////////////////////////////for Buttons
        btnForward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(forward.getBytes());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnBackward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(backward.getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(left.getBytes());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(right.getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnGoUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(goUp.getBytes());

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnGoDown.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(goDown.getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(stop.getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});
    }


    private void askPermission() {// permission for floating widget
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        //startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutExpanded:
                //switching views
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;

            case R.id.buttonClose:
                //closing the widget
                stopSelf();
                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////copied from Controlling.java

}
