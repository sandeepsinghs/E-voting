package example.com.e_voting.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import AppConfig.LinkConfig;
import AppConfig.MySingleton;
import example.com.e_voting.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentRegister.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegister extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText edtName, edtEmail, edtPassword, edtPhone, edtAddress, edtDOB, edtVotingID;
    RadioGroup radioGroup;
    Button btnSubmit;
    ImageView imgUser, imgThumb;

    ProgressDialog progressDialog;

    boolean isAvailable = false;

    private String strName, strEmail, strPassword, strPhone, strDOB, strVotingId, strAddress, strGender;
    private SharedPreferences preferences;

    public static final int REQUEST_THUMB = 11;
    public static final int REQUEST_GALLERY = 22;
    Uri mCapturedImageURI;
    File file;
    String picturePath, thumbPath;

    boolean isImageSelected = false, isThumbSelected = false;
    private OnFragmentInteractionListener mListener;

    public FragmentRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegister newInstance(String param1, String param2) {
        FragmentRegister fragment = new FragmentRegister();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        preferences = getActivity().getSharedPreferences(getActivity().getPackageName(), getActivity().MODE_PRIVATE);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Validating voter...");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_register, container, false);
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtAddress = (EditText) view.findViewById(R.id.edtPersonAddress);
        edtDOB = (EditText) view.findViewById(R.id.edtPersonDOB);
        edtEmail = (EditText) view.findViewById(R.id.edtPersonEmail);
        edtName = (EditText) view.findViewById(R.id.edtPersonName);
        edtPassword = (EditText) view.findViewById(R.id.edtPersonPassword);
        edtPhone = (EditText) view.findViewById(R.id.edtPersonPhone);
        edtVotingID = (EditText) view.findViewById(R.id.edtPersonVoterId);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        imgUser = (ImageView) view.findViewById(R.id.imgPerson);
        imgThumb = (ImageView) view.findViewById(R.id.imgThumb);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    DatePickerFragment fragment = new DatePickerFragment(edtDOB);

                    fragment.show(getActivity().getSupportFragmentManager(), "DatePicker");

                }
            }
        });


        edtVotingID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                strName = edtName.getText().toString();
                strDOB = edtDOB.getText().toString();
                strVotingId = edtVotingID.getText().toString();


                int selected = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) view.findViewById(selected);

                if (radioButton.getId() == R.id.radioMale) {

                    strGender = "Male";


                } else if (radioButton.getId() == R.id.radioFemale) {

                    strGender = "Female";

                }


                if (!hasFocus) {

                    if (!strName.equals("") || !strDOB.equals("") || !strVotingId.equals("")) {

                        validateVoter(strName, strDOB, strGender, strVotingId);


                    } else {
                        Toast.makeText(getActivity(), "Please enter above fields", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strAddress = edtAddress.getText().toString();
                strDOB = edtDOB.getText().toString();
                strEmail = edtEmail.getText().toString();
                strName = edtName.getText().toString();
                strPassword = edtPassword.getText().toString();
                strPhone = edtPhone.getText().toString();
                strVotingId = edtVotingID.getText().toString();


                int selected = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) view.findViewById(selected);

                if (radioButton.getId() == R.id.radioMale) {

                    strGender = "Male";


                } else if (radioButton.getId() == R.id.radioFemale) {

                    strGender = "Female";

                }


                if (strVotingId.equals("")) {

                    Toast.makeText(getActivity(), "Enter Voter ID", Toast.LENGTH_SHORT).show();

                } else if (strPhone.equals("") || strPhone.length() < 10) {

                    Toast.makeText(getActivity(), "Enter Valid Phone Number", Toast.LENGTH_SHORT).show();

                } else if (strPassword.equals("")) {
                    Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (strAddress.equals("")) {
                    Toast.makeText(getActivity(), "Enter Address", Toast.LENGTH_SHORT).show();
                } else if (strDOB.equals("")) {
                    Toast.makeText(getActivity(), "Enter Date of Birth", Toast.LENGTH_SHORT).show();
                } else if (strName.equals("")) {

                    Toast.makeText(getActivity(), "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (!LinkConfig.EMAIL_ADDRESS_PATTERN.matcher(strEmail).matches()) {

                    Toast.makeText(getActivity(), "Enter Valid Email address", Toast.LENGTH_SHORT).show();
                } else if (!isImageSelected) {
                    Toast.makeText(getActivity(), "Please select profile image", Toast.LENGTH_SHORT).show();
                } else if (!isThumbSelected) {

                    Toast.makeText(getActivity(), "Please select thumb image", Toast.LENGTH_SHORT).show();
                } else {

                    if (isAvailable) {
                        preferences.edit().putString(LinkConfig.USERNAME, strName)
                                .putString(LinkConfig.EMAIL, strEmail)
                                .putString(LinkConfig.ADDRESS, strAddress)
                                .putString(LinkConfig.DOB, strDOB)
                                .putString(LinkConfig.MOBILE, strPhone)
                                .putString(LinkConfig.VOTER_ID, strVotingId)
                                .putString(LinkConfig.IMAGE_STRING, picturePath)
                                .putString(LinkConfig.THUMB_STRING, thumbPath)
                                .putString(LinkConfig.GENDER, strGender)
                                .putString(LinkConfig.PASSWORD, strPassword).apply();


                        onButtonPressed(null);
                    } else {

                        Toast.makeText(getActivity(), "User details are not available", Toast.LENGTH_SHORT).show();

                    }


                }


            }
        });


        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent1.setType("image/*");

                startActivityForResult(Intent.createChooser(intent1, "Select Image"), REQUEST_GALLERY);

            }
        });


        imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent1.setType("image/*");

                startActivityForResult(Intent.createChooser(intent1, "Select Image"), REQUEST_THUMB);
            }
        });


    }

    private void validateVoter(final String strName, final String strDOB, final String strGender, final String strVotingId) {


        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, LinkConfig.VALIDATE_VOTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("######", "Rsponse  : " + response);

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("available").equals("Y")) {
                        isAvailable = true;

                        Toast.makeText(getActivity(), "User available", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(getActivity(), "User details are not available", Toast.LENGTH_SHORT).show();

                        isAvailable = false;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("######", "Error  : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                params.put("fName", strName);
                params.put("dob", strDOB);
                params.put("gender", strGender);
                params.put("vcNo", strVotingId);

                Log.d("#####", "Params : " + params.toString());

                return params;
            }
        };

        MySingleton.getInstance(getActivity()).addToRequestQueue(request);


    }

    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(contentUri, filePath, null, null, null);

            int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //c.getColumnIndex(filePath[0]);
            c.moveToFirst();
            return c.getString(columnIndex);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getActivity(), "onActivityResult", Toast.LENGTH_SHORT).show();

        if (resultCode == getActivity().RESULT_OK) {

            if (requestCode == REQUEST_GALLERY) {
                Toast.makeText(getActivity(), "result code " + requestCode, Toast.LENGTH_SHORT).show();
                isImageSelected = true;

                Uri selectedImage = data.getData();

                picturePath = getRealPathFromURI(selectedImage);

                Log.d("######", "picture path " + picturePath);

                Bitmap bmp2 = (BitmapFactory.decodeFile(picturePath));

                imgUser.setImageBitmap(bmp2);
            } else if (requestCode == REQUEST_THUMB) {


                Toast.makeText(getActivity(), "result code " + requestCode, Toast.LENGTH_SHORT).show();
                isThumbSelected = true;

                Uri selectedImage = data.getData();

                thumbPath = getRealPathFromURI(selectedImage);

                Log.d("######", "picture path " + thumbPath);

                Bitmap bmp2 = (BitmapFactory.decodeFile(thumbPath));

                imgThumb.setImageBitmap(bmp2);


            }


        }


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
}
