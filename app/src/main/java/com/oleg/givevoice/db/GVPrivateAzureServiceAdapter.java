package com.oleg.givevoice.db;

import android.content.Context;

import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class GVPrivateAzureServiceAdapter extends GVAzureServiceAdapter {

    private static GVPrivateAzureServiceAdapter mInstance = null;

    private GVPrivateAzureServiceAdapter(Context context) {
        super(context);
        this.getClient().withFilter(
                new ServiceFilter() {
                    @Override
                    public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilter) {

                        // Set custom header
                        request.addHeader("CUSTOM_HEADER2","HEADER_VALUE2");

                        ListenableFuture<ServiceFilterResponse> responseFuture = nextServiceFilter.onNext(request);
                        return responseFuture;
                    }
                }
        );
    }


    public static void Initialize(Context context) {
        if (mInstance == null) {
            mInstance = new GVPrivateAzureServiceAdapter(context);
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
}
