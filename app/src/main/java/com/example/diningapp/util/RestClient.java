package com.example.diningapp.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestClient {

    private static final OkHttpClient client = new OkHttpClient();
    /**
     * FIXME: Should replace with AsycnTask?
     * @param url
     */
    public Optional<String> request(String url) throws InterruptedException, ExecutionException, TimeoutException {
        String json;
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> future = executor.submit(new Work(url));
        json = future.get(10, TimeUnit.SECONDS);
        return StringUtils.isNotBlank(json) ? Optional.of(json) : Optional.empty();
    }

    class Work implements Callable<String> {
        private final String url;
        private  String result = "";

        public Work(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            Request request = new Request.Builder()
                        .url(url)
                        .build();

            try (Response response = client.newCall(request).execute()) {
                result =  response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}