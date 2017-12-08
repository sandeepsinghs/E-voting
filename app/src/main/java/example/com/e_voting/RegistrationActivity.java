package example.com.e_voting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import example.com.e_voting.Fragments.FragmentRegister;
import example.com.e_voting.Fragments.FragmentVerifyPerson;

public class RegistrationActivity extends AppCompatActivity implements FragmentRegister.OnFragmentInteractionListener, FragmentVerifyPerson.OnFragmentInteractionListener {

    private static final int PERMISSION_REQUEST_CODE = 101;
    FragmentManager mFragmentManager;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        if (Build.VERSION.SDK_INT>=23){

           if (checkPermission()){



           }else {
               requestPermission();
           }

        }

        mFragmentManager = getSupportFragmentManager();

        fragment = FragmentRegister.newInstance("", "");


        mFragmentManager.beginTransaction().add(R.id.container, fragment).commit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {


        fragment = FragmentVerifyPerson.newInstance("", "");
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack("");
        ft.commit();



    }



    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){

            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }




}
