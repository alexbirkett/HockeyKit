package net.hockeyapp.android;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;

public class CheckUpdateTaskNoGui extends AsyncTask<String, String, JSONArray>{
  
	
  private Context context = null;
  private String urlString = null;
  private String appIdentifier = null;
  private UpdateTaskObserver observer;
  
 
  
  public interface UpdateTaskObserver {
	  void onUpdateAvailable(UpdateDetails result);
  }
  
  public CheckUpdateTaskNoGui(Context context, String urlString, String appIdentifier, UpdateTaskObserver observer) {
    this.appIdentifier = appIdentifier;
    this.context = context;
    this.urlString = urlString;
    this.observer = observer;
    Constants.loadFromContext(context);
  }
  
  @Override
  protected JSONArray doInBackground(String... args) {
    try {
      int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA).versionCode;
      
      URL url = new URL(getURLString("json"));
      URLConnection connection = url.openConnection();
      connection.addRequestProperty("User-Agent", "Hockey/Android");
      connection.setRequestProperty("connection", "close");
      connection.connect();

      InputStream inputStream = new BufferedInputStream(connection.getInputStream());
      String jsonString = convertStreamToString(inputStream);
      inputStream.close();
      
      JSONArray json = new JSONArray(jsonString);
      for (int index = 0; index < json.length(); index++) {
        JSONObject entry = json.getJSONObject(index);
        if (entry.getInt("version") > versionCode) {
          return json;
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }

	@Override
	protected void onPostExecute(JSONArray updateInfo) {
		if (updateInfo != null) {
			UpdateDetails result = new UpdateDetails();
			result.setJson(updateInfo.toString());
			result.setUrl(getURLString("apk"));
			observer.onUpdateAvailable(result);
		}
	}
  
  private String getURLString(String format) {
    StringBuilder builder = new StringBuilder();
    builder.append(urlString);
    builder.append("api/2/apps/");
    builder.append((this.appIdentifier != null ? this.appIdentifier : context.getPackageName()));
    builder.append("?format=" + format);
    String udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    builder.append("&udid=" + URLEncoder.encode(udid));
    builder.append("&os=Android");
    builder.append("&os_version=" + URLEncoder.encode(Constants.ANDROID_VERSION));
    builder.append("&device=" + URLEncoder.encode(Constants.PHONE_MODEL));
    builder.append("&oem=" + URLEncoder.encode(Constants.PHONE_MANUFACTURER));
    builder.append("&app_version=" + URLEncoder.encode(Constants.APP_VERSION));
    
    return builder.toString();
  }
  
  
  private static String convertStreamToString(InputStream inputStream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
    StringBuilder stringBuilder = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line + "\n");
      }
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    finally {
      try {
        inputStream.close();
      } 
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return stringBuilder.toString();
  }
}