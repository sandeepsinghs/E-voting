package example.com.e_voting.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by AAE on 1/6/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    EditText editText;



    public DatePickerFragment(EditText editText){

        this.editText = editText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


    DatePickerDialog d = new DatePickerDialog(getActivity(),this,year,month,day);

       /* DatePicker dp = d.getDatePicker();

        dp.setMinDate(c.getTimeInMillis());*/


        return d;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String day = "",month="";


        if (dayOfMonth<10){

            day = "0"+dayOfMonth;
        }else {
            day = String.valueOf(dayOfMonth);
        }

        if (monthOfYear+1<10){

            month = "0"+(monthOfYear+1);

        }else {
            month = String.valueOf(monthOfYear+1);
        }


       editText.setText(day + "-" +month + "-" + year);

        editText.clearFocus();

        Log.d("####", "## date : " + year + ": " + (monthOfYear+1) + " :" + dayOfMonth);
    }
}
