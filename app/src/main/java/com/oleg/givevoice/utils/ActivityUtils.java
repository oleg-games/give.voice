package com.oleg.givevoice.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import com.oleg.givevoice.exceptions.GVException;

import java.util.List;

public class ActivityUtils {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View mMainFormView, final View mProgressView, Resources resources) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);

            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mMainFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final List<View> mMainViews, final List<View> mProgressViews, Resources resources) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime);

            for (final View view: mMainViews) {
                showView(show, view, shortAnimTime);
            }

            for (final View view: mProgressViews) {
                showView(!show, view, shortAnimTime);
            }
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            for (View view: mProgressViews) {
                view.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            for (View view: mMainViews) {
                view.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

    private static void showView(final boolean show, final View view, int shortAnimTime) {
        view.setVisibility(show ? View.GONE : View.VISIBLE);
        view.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialogOnUI(final Activity activity, final Exception exception, final String title) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(activity, exception, title);
            }
        });
    }

    /**
     * Creates a dialog and shows it
     *
     * @param activity
     *            The activity where show dialog
     * @param exception
     *            The exception to show in the dialog
     */
    public static void createAndShowDialogOnUI(final Activity activity, final GVException exception) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(activity, exception, exception.getTitle());
            }
        });
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialog(Context context, Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(context, ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private static void createAndShowDialog(final Context context, final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
