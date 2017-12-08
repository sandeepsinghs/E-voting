package AppConfig;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AAE on 1/7/2016.
 */
public class ParseJson {



  public ParseJson(){

  }

    public JSONObject getJSONFromString(String response) throws JSONException {

        String  str = response.substring(response.indexOf("{"),response.lastIndexOf("<"));

        JSONObject jsonObject = new JSONObject(str);




        return jsonObject;
    }

    public JSONArray getJSONArrayFromString(String response) throws JSONException {

        String  str = response.substring(response.indexOf("["),response.lastIndexOf("<"));

        JSONArray jsonArray = new JSONArray(str);




        return jsonArray;
    }


    public String getDate(String timeStampStr){


        try{
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date netDate = (new Date(Long.parseLong(timeStampStr)));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }



}
