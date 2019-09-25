package com.ph.phome;


import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;

/**
 * Created by yang on 28/08/17.
 */

class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        if (BuildConfig.DEBUG) {
            Log.d(LoggingInterceptor.class.getSimpleName(), String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
        }

            Response response = chain.proceed(request);
            if (BuildConfig.DEBUG) {
                long t2 = System.nanoTime();
                Log.d(LoggingInterceptor.class.getSimpleName(), String.format("Received response for %s in %.1fms%n%s",
                        response.request().url(), (t2 - t1) / 1e6d, response.headers()));
//
                ResponseBody responseBody = copyResponseBody(response.body(), Long.MAX_VALUE);

                Log.d(LoggingInterceptor.class.getSimpleName(), responseBody.string());
            }
            return response;
        }


        private ResponseBody copyResponseBody (ResponseBody body, long limit) throws IOException {
            BufferedSource source = body.source();
            if (source.request(limit)) throw new IOException("body too long!");
            Buffer bufferedCopy = source.buffer().clone();
            return ResponseBody.create(body.contentType(), body.contentLength(), bufferedCopy);
        }
}