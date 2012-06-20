package net.hockeyapp.android;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class UpdateDialogBuiler {
	
	public static void showDialog(final Activity activity, final UpdateDetails updateDetails) {
	    if ((activity == null) || (activity.isFinishing())) {
	      return;
	    }
	    
	    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
	    builder.setTitle(R.string.update_dialog_title);
	    builder.setMessage(R.string.update_dialog_message);

	    builder.setNegativeButton(R.string.update_dialog_negative_button, new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	      } 
	    });
	    
	    builder.setPositiveButton(R.string.update_dialog_positive_button, new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        Intent intent = new Intent(activity, UpdateActivity.class);
	        intent.putExtra("json", updateDetails.getJson());
	        intent.putExtra("url", updateDetails.getUrl());
	        activity.startActivity(intent);
	      } 
	    });
	    
	    builder.create().show();
	  }

}
