package com.oleg.givevoice.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.oleg.givevoice.R;
import com.oleg.givevoice.db.GVPublicAzureServiceAdapter;
import com.oleg.givevoice.db.gvverificationuser.GVVerificationUser;
import com.oleg.givevoice.tasks.GVTask;
import com.oleg.givevoice.utils.PhoneUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginTask mTask;
    private Activity mActivity;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<GVVerificationUser> mVerificationUser;

    private EditText mPhoneView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            GVPublicAzureServiceAdapter.Initialize(this);
        } catch (IllegalStateException e) {
            // TODO
        }

        GVPublicAzureServiceAdapter servicemAdapter = GVPublicAzureServiceAdapter.getInstance();
        MobileServiceClient mClient = servicemAdapter.getClient();
        mVerificationUser = mClient.getTable(GVVerificationUser.class);
        mActivity = this;
        mPhoneView = findViewById(R.id.phoneNumber);

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView mPasswordView = findViewById(R.id.version_text_view);
            mPasswordView.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

//        // Reset errors.
//        mEmailView.setError(null);
//        mPasswordView.setError(null);

//        // Store values at the time of the login attempt.
//        String email = mEmailView.getText().toString();
//        String password = mPasswordView.getText().toString();
        String phone = PhoneUtils.getPhoneNumber(mPhoneView.getText().toString());

        boolean cancel = false;
        View focusView = null;

//        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
//            mPasswordView.setError(getString(R.string.error_invalid_password));
//            focusView = mPasswordView;
//            cancel = true;
//        }

//        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            mEmailView.setError(getString(R.string.error_field_required));
//            focusView = mEmailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        // Check for a valid phone.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            mAuthTask = new UserLoginTask(email, password);
            mTask = new LoginTask(Arrays.asList(mLoginFormView), Arrays.asList(mProgressView), getResources(), this, phone);
            mTask.execute();
        }
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public GVVerificationUser addItemInTable(GVVerificationUser item) throws ExecutionException, InterruptedException {
        GVVerificationUser entity = mVerificationUser.insert(item).get();
        return entity;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoginTask extends GVTask {

        Context mContext;
        String mPhone;

        LoginTask(List<View> mainViews, List<View> progressViews, Resources resources, Context context, String phone) {
            super(mActivity, mainViews, progressViews, resources);
            this.mPhone = phone;
            this.mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (super.doInBackground(params)) {
                try {
                    final GVVerificationUser item = new GVVerificationUser();
                    item.setUserPhone(mPhone);

                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(mContext);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("phone", mPhone);
                    editor.commit();

                    addItemInTable(item);
                    Intent intent = new Intent(mActivity, VerifyCodeLoginActivity.class);
                    startActivity(intent);

                    return true;
                } catch (final Exception e){
                    createAndShowDialog(e, "Error");
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mTask = null;
        }


        @Override
        protected void onCancelled() {
            super.onCancelled();
            mTask = null;
        }
    }
}

