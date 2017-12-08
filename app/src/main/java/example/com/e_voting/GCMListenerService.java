package example.com.e_voting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AAE on 1/12/2016.
 */
public class GCMListenerService extends GcmListenerService {

    JSONObject object;
   public static final int  NOTIFICATION_ID = 5252;


    private int electionId;
    private String name;

    String tickertxt="Election Notification",title="Demo";
    Uri sound;
    @Override
    public void onCreate() {
        super.onCreate();



        sound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    }


    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Log.d("####", "GCM message from : " + from);
        Log.d("####", "GCM message data : " + data.toString());




        createNotification(data);


    }

 public void createNotification(Bundle data){

     Context context = getBaseContext();

     String str = data.toString();
     str = str.substring(str.indexOf("=")+1,str.lastIndexOf(","));


     try {
          object = new JSONObject(str);

       electionId = object.getInt("electionId");
         name = object.getString("electionName");
         title = object.getString("venue");




    Log.d("###","ticker :"+tickertxt+" title : "+title);


     } catch (JSONException e) {
         e.printStackTrace();
     }


     Intent intent = new Intent(GCMListenerService.this, CandidatesActivity.class);

     intent.putExtra("name",name);
     intent.putExtra("id",electionId);

     PendingIntent pendingIntent = PendingIntent.getActivity(GCMListenerService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);



     NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setTicker(tickertxt)
             .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).setSound(sound).setAutoCancel(true)
             .setContentTitle("Election Initiated at "+title).setContentText("For "+name);


     NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

mNotificationManager.notify(NOTIFICATION_ID,mBuilder.build());



 }



}
