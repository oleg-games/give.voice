package com.oleg.givevoice.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class GVPrivateAzureServiceAdapter {
    private String mMobileBackendUrl = "https://givevoice.azurewebsites.net";
    private Context mContext;
    private MobileServiceClient mClient;

    public MobileServiceClient getClient() {
        return mClient;
    }

    // Place any public methods that operate on mClient here.
    private static GVPrivateAzureServiceAdapter mInstance = null;

    public static void Initialize(Context context) {
        if (mInstance == null) {
            mInstance = new GVPrivateAzureServiceAdapter (context);
        } else {
            throw new IllegalStateException("AzureServiceAdapter is already initialized");
        }
    }

    public static GVPrivateAzureServiceAdapter getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("AzureServiceAdapter is not initialized");
        }
        return mInstance;
    }

    private GVPrivateAzureServiceAdapter(final Context context) {
        mContext = context;
        try {
            mClient = new MobileServiceClient(mMobileBackendUrl, mContext).withFilter(
                    new ServiceFilter() {
                        @Override
                        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilter) {
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                            String token = "";

                            if (settings.contains("token")) {
                                token = settings.getString("token", "");
                            }

                            // Set custom header
                            request.addHeader("token", token);

                            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

                            ListenableFuture<ServiceFilterResponse> future = nextServiceFilter.onNext(request);
                            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onFailure(Throwable e) {
                                    resultFuture.setException(e);
                                }
                                @Override
                                public void onSuccess(ServiceFilterResponse response) {
                                    resultFuture.set(response);
                                }
                            });
                            return resultFuture;
                        }
                    });
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
