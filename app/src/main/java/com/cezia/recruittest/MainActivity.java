package com.cezia.recruittest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.cezia.recruittest.structure.CaseRecordAPI;
import com.cezia.recruittest.structure.CaseRecordList;
import com.cezia.recruittest.structure.CaseRecordListAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.cezia.recruittest.Utils.createRequest;

public class MainActivity extends AppCompatActivity {
    RecyclerView vRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vRecView = (RecyclerView) findViewById(R.id.recycler_view);
        //creating a request for data from the server
        Observable observable = createRequest("https://sandbox.1click2deliver.com:10999/panel/proxy.php/?action=getStatuses");
        observable = observable.subscribeOn(Schedulers.io());
        observable = observable.observeOn(AndroidSchedulers.mainThread());
        //subscription to data received from the server
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(String s) {
                //parsing data from a Jason string to a list of data api-objects
                Type collectionType = new TypeToken<List<CaseRecordAPI>>() {
                }.getType();
                List<CaseRecordAPI> list = new Gson().fromJson(s, collectionType);
                CaseRecordListAPI caseRecordListAPI = new CaseRecordListAPI(list);

                //converting api-objects into data storage objects in the database Realm
                final CaseRecordList caseRecordList = new CaseRecordList();
                caseRecordList.convertFromAPIList(caseRecordListAPI);

                //transaction of entering data from db-objects into the database Realm
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //The previous data from the database is deleted.
                        RealmResults<CaseRecordList> realmold = realm.where(CaseRecordList.class).findAll();
                        if (!realmold.isEmpty()) {
                            for (CaseRecordList record : realmold) {
                                record.deleteFromRealm();
                            }
                        }
                        //copying a list of db-objects into the database Realm
                        realm.copyToRealm(caseRecordList);
                    }
                });
                //TODO show all records from the database in UI
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("debug", "onError: " + throwable);
                //TODO when an error is still shown all old records from the database in UI
            }

            @Override
            public void onComplete() {
            }
        });
    }
}
