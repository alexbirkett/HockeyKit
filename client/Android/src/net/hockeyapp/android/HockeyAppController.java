package net.hockeyapp.android;

import android.app.Activity;
import android.os.Bundle;
import no.birkettconsulting.controllers.Controller;

public class HockeyAppController extends Controller implements CheckUpdateTaskNoGui.UpdateTaskObserver {
	
	private CheckUpdateTaskNoGui checkUpdateTask;
	private String downloadUrl;
	private String appId;
	private boolean paused;

	/**
	 * Using a static instance variable is not ideal! It was done because the
	 * onRetainNonConfigurationInstance() method is deprecated and not available
	 * if the BaseControllerActivity extends FragmentActivity. It would be
	 * possible to use a Fragment and retain the instance across Activity
	 * recreation. Alternatively, a combination of a controller and a service
	 * could be used instead but the service would need to be declared in the
	 * app's AndroidManifest which is additional work for the developer.
	 */
	private static UpdateDetails updateDetails;
	
	public HockeyAppController(Activity activity, String downloadUrl, String appId) {
		super(activity);
		this.appId = appId;
		this.downloadUrl = downloadUrl;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		checkForUpdates();
	}
	
	private void checkForUpdates() {
		if (HockeyAppController.updateDetails == null) {
			checkUpdateTask = new CheckUpdateTaskNoGui(getActivity(), downloadUrl, appId, this);
			checkUpdateTask.execute();			
		}
	}

	private void showUpdateAvailableDialogIfUpdateDetailsSet() {
		if (HockeyAppController.updateDetails != null) {
			UpdateDialogBuiler.showDialog(getActivity(),
					HockeyAppController.updateDetails);
			HockeyAppController.updateDetails = null;
		}
		
	}
	
	@Override
	public void onPause() {
		paused = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		paused = false;
		checkForCrashes();
		showUpdateAvailableDialogIfUpdateDetailsSet();
	}

	private void checkForCrashes() {
		CrashManager.register(mContext,downloadUrl, appId);
	}

	private Activity getActivity() {
		return (Activity) mContext;
	}

	@Override
	public void onUpdateAvailable(UpdateDetails details) {
		if (paused) {
			HockeyAppController.updateDetails = details;
		} else {
			UpdateDialogBuiler.showDialog(getActivity(), details);
		}
	}

}
