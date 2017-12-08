package example.com.e_voting.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import AppConfig.LinkConfig;
import AppConfig.MultipartRequest;
import AppConfig.MySingleton;
import AppConfig.ParseJson;
import example.com.e_voting.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentVerifyPerson.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentVerifyPerson#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentVerifyPerson extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String strName, strEmail, strPassword, strPhone, strDOB, strVotingId, strAddress, strOTP, strImagePath, strGender, strThumbpath, strUIID;
    private SharedPreferences preferences;

    private EditText edtOPT;
    private Button btnOk;
    private TextView txtResendOtp;

    private ParseJson parseJson = new ParseJson();
    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;

    public FragmentVerifyPerson() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentVerifyPerson.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentVerifyPerson newInstance(String param1, String param2) {
        FragmentVerifyPerson fragment = new FragmentVerifyPerson();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public int generateRandom() {
        int iRandom = 0;
        for (int i = 0; i < 50; ++i) {
            iRandom = new Random().nextInt(9999 - 1000) + 1000;
        }
        return iRandom;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        preferences = getActivity().getSharedPreferences(getActivity().getPackageName(), getActivity().MODE_PRIVATE);


        strAddress = preferences.getString(LinkConfig.ADDRESS, "");
        strDOB = preferences.getString(LinkConfig.DOB, "");
        strEmail = preferences.getString(LinkConfig.EMAIL, "");
        strName = preferences.getString(LinkConfig.USERNAME, "");
        strPassword = preferences.getString(LinkConfig.PASSWORD, "");
        strPhone = preferences.getString(LinkConfig.MOBILE, "");
        strVotingId = preferences.getString(LinkConfig.VOTER_ID, "");
        strImagePath = preferences.getString(LinkConfig.IMAGE_STRING, "");
        strGender = preferences.getString(LinkConfig.GENDER, "");
        strThumbpath = preferences.getString(LinkConfig.THUMB_STRING, "");
        strUIID = preferences.getString(LinkConfig.UIID, "");


        progressDialog = new ProgressDialog(getActivity());

        sendOTP();

    }

    private void sendOTP() {

        SmsManager manager = SmsManager.getDefault();

        strOTP = String.valueOf(generateRandom());

        Log.d("########", "otp :" + strOTP);
        String message = "Your one time OTP for Registering in E-Voting is " + strOTP + "\nKindly do not share this with any one";

        sendOTP(strPhone, message);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_verify_person, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtOPT = (EditText) view.findViewById(R.id.edtOTP);
        btnOk = (Button) view.findViewById(R.id.btnOk);
        txtResendOtp = (TextView) view.findViewById(R.id.txtResendOtp);


        txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendOTP();

                txtResendOtp.setVisibility(View.INVISIBLE);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("#####", strName + "\n" + strVotingId + "\n" + strPhone + "\n" + strAddress + "\n" + strEmail + "\n" + strImagePath + "\n" + strDOB + "\n" + strGender);


                if (strOTP.equals(edtOPT.getText().toString())) {


                    doRegister();


                } else {

                    Toast.makeText(getActivity(), "Invalid OTP entered", Toast.LENGTH_SHORT).show();

                    // doRegister();

                }


            }
        });


    }

    private void doRegister() {

        final HashMap<String, String> params = new HashMap<>();

        params.put("fullName", strName);
        params.put("address", strAddress);
        params.put("dob", strDOB);
        params.put("userName", strEmail);
        params.put("gender", strGender);
        params.put("password", strPassword);
        params.put("mobileNumber", strPhone);
        params.put("votingCardNumber", strVotingId);
        params.put("uiId", strUIID);

        progressDialog.setMessage("Registering.......");
        progressDialog.show();


        MultipartRequest request = new MultipartRequest(LinkConfig.REGISTER, strImagePath, strThumbpath, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    JSONObject object = new JSONObject(response);

                    Log.d("#####", "Response  : " + object.toString());


                    if (object.getString("result").equals("success")) {

                        Toast.makeText(getActivity(), "Registered Successfully", Toast.LENGTH_SHORT).show();

                        getActivity().finish();

                    } else {
                        // String message = object.getString("Message");

                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("#####", "Exception  : " + e.toString());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("#####", "Error   : " + error.toString());

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getActivity()).addToRequestQueue(request);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void sendOTP(final String mobile, final String message) {


        progressDialog.setMessage("Sending OTP on your registered mobile");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, getResources().getString(R.string.SMSURL), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("msg", message);
                params.put("to", mobile);

                params.put("user", getResources().getString(R.string.SMS_User));
                params.put("pwd", getResources().getString(R.string.PWD));
                params.put("fl", getResources().getString(R.string.FL));
                params.put("sid", getResources().getString(R.string.SID));


                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(request);


    }
}
