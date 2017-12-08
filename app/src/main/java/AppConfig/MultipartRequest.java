package AppConfig;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class MultipartRequest extends Request<String> {
    public static final String KEY_PICTURE = "photo";
    public static final String KEY_THUMB = "thumb";
    public static final String KEY_PICTURE_NAME = "filename";
    public static final String KEY_ROUTE_ID = "route_id";
    JSONObject object;
    private HttpEntity mHttpEntity;
    private HashMap<String, String> mParams;
    private String mRouteId;
    private Response.Listener mListener;


    public MultipartRequest(String url, String filePath, HashMap<String, String> params,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mParams = params;
        mListener = listener;
        mHttpEntity = buildMultipartEntity(filePath);
    }

    public MultipartRequest(String url, String filePath, String filePath2, HashMap<String, String> params,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mParams = params;
        mListener = listener;
        mHttpEntity = buildMultipartEntity2(filePath, filePath2);

    }

    private HttpEntity buildMultipartEntity(String filePath) {
        File file = new File(filePath);
        return buildMultipartEntity(file);
    }

    private HttpEntity buildMultipartEntity(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        FileBody fileBody = new FileBody(file);


        if (mParams != null) {

            for (Map.Entry<String, String> entry : mParams.entrySet()) {

                Log.d("####", "params :" + entry.getKey() + ": " + entry.getValue());


                builder.addTextBody(entry.getKey(), entry.getValue());
            }

        }

        builder.addPart(KEY_PICTURE, fileBody);

      /*  builder.addTextBody(KEY_PICTURE_NAME, fileName);
        builder.addTextBody(KEY_ROUTE_ID, mRouteId);*/
        return builder.build();
    }

    private HttpEntity buildMultipartEntity2(String filePath, String filePath2) {
        File file = new File(filePath);
        File file2 = new File(filePath2);
        return buildMultipartEntity2(file, file2);
    }

    private HttpEntity buildMultipartEntity2(File file, File file2) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        FileBody fileBody = new FileBody(file);
        FileBody fileBody2 = new FileBody(file2);


        if (mParams != null) {

            for (Map.Entry<String, String> entry : mParams.entrySet()) {

                Log.d("####", "params :" + entry.getKey() + ": " + entry.getValue());


                builder.addTextBody(entry.getKey(), entry.getValue());
            }

        }

        builder.addPart(KEY_PICTURE, fileBody);
        builder.addPart(KEY_THUMB, fileBody2);


      /*  builder.addTextBody(KEY_PICTURE_NAME, fileName);
        builder.addTextBody(KEY_ROUTE_ID, mRouteId);*/
        return builder.build();
    }


    @Override
    public String getBodyContentType() {
        return mHttpEntity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mHttpEntity.writeTo(bos);

        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        Log.d("###", "Response from file upload :" + response.headers + "\ndata :" + response.data.toString() + "\n" +
                "Data String: " + new String(response.data));


        String jsonString = "";
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Log.e("####", "JSon String " + jsonString);

            object = new JSONObject(jsonString);

            Response.success(object, HttpHeaderParser.parseCacheHeaders(response));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("######", "Exception 1 " + e.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("######", "Exception 2 " + e.toString());
        }

        return Response.success(object.toString(), getCacheEntry());

    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}