package net.hockeyapp.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import no.birkettconsulting.controllers.Controller;

public class HockeyAppController extends Controller {
	
	private CheckUpdateTask checkUpdateTask;

	private String mDownloadUrl;
	private String mAppId;

	public HockeyAppController(Activity activity, String downloadUrl, String appId) {
		super(activity);
		mAppId = appId;
		mDownloadUrl = downloadUrl;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		checkForUpdates();
	}
	
	private void checkForUpdates() {
		checkUpdateTask = (CheckUpdateTask) getActivity()
				.getLastCustomNonConfigurationInstance();
		if (checkUpdateTask != null) {
			checkUpdateTask.attach(getActivity());
		} else {
			checkUpdateTask = new CheckUpdateTask(getActivity(),
					mDownloadUrl, mAppId);
			checkUpdateTask.execute();
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		checkUpdateTask.detach();
		return checkUpdateTask;
	}

	@Override
	public void onResume() {
		super.onResume();
		checkForCrashes();
	}

	private void checkForCrashes() {
		CrashManager.register(mContext,mDownloadUrl, mAppId);
	}

	private FragmentActivity getActivity() {
		return (FragmentActivity) mContext;
	}

}
