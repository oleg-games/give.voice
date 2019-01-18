package com.oleg.givevoice.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class GVPublicAzureServiceAdapter {
    private String mMobileBackendUrl = "https://givevoice.azurewebsites.net";
    private Context mContext;
    private MobileServiceClient mClient;

    public MobileServiceClient getClient() {
        return mClient;
    }

    // Place any public methods that operate on mClient here.
    private static GVPublicAzureServiceAdapter mInstance = null;

    public static void Initialize(Context context) {
        if (mInstance == null) {
            mInstance = new GVPublicAzureServiceAdapter(context);
        } else {
            throw new IllegalStateException("AzureServiceAdapter is already initialized");
        }
    }

    public static GVPublicAzureServiceAdapter getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("AzureServiceAdapter is not initialized");
        }
        return mInstance;
    }

    private GVPublicAzureServiceAdapter(final Context context) {
        mContext = context;
        try {
            mClient = new MobileServiceClient(mMobileBackendUrl, mContext).withFilter(
                    new ServiceFilter() {
                        @Override
                        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilter) {
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                            String phone = "";

                            if (settings.contains("phone")) {
                                phone = settings.getString("phone", "");
                            }

                            String code = "";

                            if (settings.contains("code")) {
                                phone = settings.getString("code", "");
                            }

                            // Set custom header
                            request.addHeader("PHONE", phone);
                            request.addHeader("CODE", code);

                            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();

                            ListenableFuture<ServiceFilterResponse> future = nextServiceFilter.onNext(request);
                            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                                @Override
                                public void onFailure(Throwable e) {
                                    resultFuture.setException(e);
                                }
                                @Override
                                public void onSuccess(ServiceFilterResponse response) {
                                    System.out.println("13213");
                                    String token = response.getHeaders().get("token");
                                    if (!token.isEmpty()) {
                                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("token", token);
                                        editor.commit();
                                    }
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

//                    ListenableFuture<ServiceFilterResponse> responseFuture = nextServiceFilter.onNext(request);

//                    Futures.addCallback(responseFuture, new FutureCallback<ServiceFilterResponse>() {
//                        @Override
//                        public void onSuccess(ServiceFilterResponse result) {
//                            System.out.print("a");
//
//                        }
//
//                        @Override
//                        public void onFailure(Throwable exc) {
////                            error.setError(ErrorHelper.Error.NETWORK_ERROR);
//                        }
//                    });

//                    return responseFuture;
//                    SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
//                    try {
//                        ServiceFilterResponse response = nextServiceFilter.onNext(request).get();
//                        result.set(response);
//
//                    } catch (Exception exc) {
//                        result.setException(exc);
//                    }
//                    return result;
//                }


//            }


//        @Override
//        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback next) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    request.addHeader("X-APIM-Router", "mobileBackend");
//                }
//            });
//            SettableFuture<ServiceFilterResponse> result = SettableFuture.create();
//            try {
//                ServiceFilterResponse response = next.onNext(request).get();
//                result.set(response);
//            } catch (Exception exc) {
//                result.setException(exc);
//            }
//        }
//        );
