package com.oleg.givevoice.tasks;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.view.View;

import com.oleg.givevoice.exceptions.GVException;

import java.util.List;

import static com.oleg.givevoice.utils.ActivityUtils.showProgress;
import static com.oleg.givevoice.utils.ActivityUtils.createAndShowDialogOnUI;

public class GVTask extends AsyncTask<Void, Void, Boolean> {

    List<View> mMainViews;
    List<View> mProgressViews;
    Resources mResources;
    Activity mActivity;

    protected GVTask(Activity activity, List<View> mainViews, List<View> progressViews, Resources resources) {
        this.mMainViews = mainViews;
        this.mProgressViews = progressViews;
        this.mResources = resources;
        this.mActivity = activity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        super.onPostExecute(success);
        showProgress(false, mMainViews, mProgressViews, mResources);
        try {
            if (!success) {
                throw new GVException("Problem when upload my questions");
            }
        } catch (GVException e) {
            createAndShowDialog(e, "Error");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgress(true, mMainViews, mProgressViews, mResources);
    }

    @Override
    protected void onCancelled() {
        showProgress(false, mMainViews, mProgressViews, mResources);
    }

    protected void createAndShowDialog(final Exception exception, final String title) {
        createAndShowDialogOnUI(mActivity, exception, title);
    }
}
