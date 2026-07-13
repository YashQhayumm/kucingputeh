package com.example.kucingputeh.remote;

import com.example.kucingputeh.KucingPutehApp;
import com.example.kucingputeh.model.User;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    // Fallback key used only when nobody is logged in yet (e.g. hitting the
    // login/register endpoints themselves). This is the organization-level
    // secret from the "organizations" table, NOT a per-user session token.
    private static final String ORG_API_KEY = "6c67db73-a21c-40f5-8ad1-657b601f5c23";

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();

                        // Look up whatever token is CURRENTLY stored, fresh, on
                        // every single request - instead of a value baked into
                        // the app at compile time. This is what lets the token
                        // "rotate" (i.e. change after every login) without
                        // ever needing to edit and rebuild the app again.
                        String tokenToSend = ORG_API_KEY;
                        try {
                            SharedPrefManager spm = new SharedPrefManager(KucingPutehApp.getAppContext());
                            User user = spm.getUser();
                            if (user != null && user.getToken() != null && !user.getToken().isEmpty()) {
                                tokenToSend = user.getToken();
                            }
                        } catch (Exception ignored) {
                            // No app context yet / nobody logged in - fall back to ORG_API_KEY above.
                        }

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("api_key", tokenToSend)
                                .method(original.method(), original.body());
                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWithoutAuth(String baseUrl) {

        retrofit = null;
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
