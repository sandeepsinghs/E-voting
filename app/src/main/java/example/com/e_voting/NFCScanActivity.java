package example.com.e_voting;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import AppConfig.LinkConfig;
import AppConfig.MySingleton;

public class NFCScanActivity extends AppCompatActivity {

    private TextView txtUIID, txtScaning;
    private ProgressBar progressBar;
    String strUIID = "";
    private ProgressDialog progressDialog;

    private static final String TAG = "######";
    private static final int RECIEVE_MESSAGE = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;

    //ToDo change the device bluetooth address
//    private static String address = "98:D3:32:20:90:60";
//    private static String address = "20:16:01:05:33:54";
//    private static String address = "98:D3:32:30:98:F5";
    private static String address = "98:D3:35:00:B5:3B";

    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    Handler handler;
    int count = 0;
    boolean checked = false;
    Context context = NFCScanActivity.this;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.

        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        System.out.println("## onResume :");
        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        Log.e(TAG, " Bluetooth device values " + device);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Log.e(TAG, " Socket inti error " + e.getMessage());
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            System.out.println("## e:" + e);
        }

        Log.e(TAG, " SCOKET " + btSocket);

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.e("######", "##...Connecting...");
        try {
            btSocket.connect();
            Log.e("####", "##....Connection ok...");
        } catch (IOException e) {
            Log.e(TAG, "CONNECTION ERROR " + e.getMessage());
            try {
                System.out.println("## socket close e :" + e);
                btSocket.close();
            } catch (IOException e2) {
                System.out.println("## IOException:" + e2);
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.e("#####", "###...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
        Log.e("#####", "###...Create thread...");

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("####", "##...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
              /*  final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);*/


                final Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                return (BluetoothSocket) m.invoke(device, 1);


            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcscan);


        init();

        btAdapter = BluetoothAdapter.getDefaultAdapter();


        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                count++;


                String readMessage = (String) msg.obj;


                sb.append(readMessage);


                if (!sb.equals("") && sb.length() > 15) {


                    progressBar.setVisibility(View.GONE);
                    txtScaning.setVisibility(View.GONE);

                    strUIID = sb.toString().substring(0, 11);

                    txtUIID.append("\n" + strUIID);


                    Log.d(TAG, "UIID: " + strUIID);

                    if (!checked) {

                        checkUIID();

                           /* startActivity(new Intent(NFCScanActivity.this, LoginActivity.class));
                            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();

                            finish();
*/
                        checked = true;
                    }
                }
            }
        };

        checkBTState();
    }

    private void checkUIID() {

        Log.e("TAG", " Now Calling server");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.VERIFY_VOTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                Log.e(TAG, "Response : " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("result").equals("fail")) {

                        getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().putString(LinkConfig.UIID, strUIID).apply();

                        startActivity(new Intent(NFCScanActivity.this, RegistrationActivity.class));

                        finish();

                    } else {

                        String email = object.getString("email");

                        startActivity(new Intent(NFCScanActivity.this, LoginActivity.class).putExtra("email", email));
                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                        finish();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.e(TAG, "ERROR VERIFY VOTER " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("uiId", strUIID);

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    private void init() {
        txtScaning = (TextView) findViewById(R.id.txtScanning);
        txtUIID = (TextView) findViewById(R.id.txtUIID);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking availability of UIID......");

    }


    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                System.out.println("## Bluetooth ON:");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[512];  // buffer store for the stream
            int bytes; // bytes returned from read()
            System.out.println("## waiting to read:");
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);// Get number of bytes and message in "buffer"
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, readMessage).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");

            System.out.println("## write message :");

            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                System.out.println("## IOException write message :" + e);
            }
        }
    }
}
