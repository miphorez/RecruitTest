package com.cezia.recruittest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class Utils {
    static Observable createRequest(final String url) {
        //creating events for an observer
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter observableEmitter) throws Exception {
                HttpURLConnection urlConnection = null;

                try {
                    urlConnection = (HttpURLConnection) new URL(url).openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                    observableEmitter.onError(new Throwable(e));
                }
                try {
                    try {
                        if (urlConnection != null) {
                            urlConnection.connect();
                        } else {
                            observableEmitter.onError(new RuntimeException("Error: Not url connection"));
                            return;
                        }
//                       Log.d("debug", "responseCode: " + urlConnection.getResponseCode());
                        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            observableEmitter.onError(new RuntimeException(urlConnection.getResponseMessage()));
                        } else {
                            //if the server responds we transfer the received data to the observer
                            InputStream input = urlConnection.getInputStream();
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            int data;
                            while ((data = input.read()) != -1) {
                                out.write(data);
                            }
                            String strResult = new String(out.toByteArray());
                            observableEmitter.onNext(strResult);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        observableEmitter.onError(new Throwable(e));
                    }
                } finally {
                    observableEmitter.onComplete();
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });


    }
}
