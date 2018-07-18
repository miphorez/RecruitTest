package com.cezia.recruittest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cezia.recruittest.structure.CaseRecord;
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
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.cezia.recruittest.Utils.createRequest;

public class MainActivity extends AppCompatActivity {
    RecyclerView vRecView;
    EditText editText;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vRecView = (RecyclerView) findViewById(R.id.recycler_view);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        if (spinner != null) {
            spinner.setVisibility(View.GONE);
        }

        editText = (EditText) findViewById(R.id.et_search);
        //remove focus from the editor editText
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                showRecView();
            }
        });

        showDataFromServer();

    }

    void showDataFromServer() {
        spinner.setVisibility(View.VISIBLE);
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
                        RealmResults<CaseRecord> realmold_records = realm.where(CaseRecord.class).findAll();
                        if (!realmold_records.isEmpty()) {
                            for (CaseRecord record : realmold_records) {
                                record.deleteFromRealm();
                            }
                        }
                        RealmResults<CaseRecordList> realmold_list = realm.where(CaseRecordList.class).findAll();
                        if (!realmold_list.isEmpty()) {
                            for (CaseRecordList record : realmold_list) {
                                record.deleteFromRealm();
                            }
                        }
                        //copying a list of db-objects into the database Realm
                        realm.copyToRealm(caseRecordList);
                    }
                });
                spinner.setVisibility(View.GONE);
                //show all records from the database in UI
                showRecView();
            }

            @Override
            public void onError(Throwable throwable) {
                spinner.setVisibility(View.GONE);
                Log.d("debug", "onError: " + throwable);
                //when an error is still shown all old records from the database in UI
                showRecView();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    void showRecView() {
        final Context context = this;
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                CaseRecordList caseRecordList;

                if (!editText.getText().toString().equals("")) {
                    RealmResults<CaseRecord> realmResults;
                    realmResults = realm.where(CaseRecord.class)
                            .contains("short_name", editText.getText().toString(), Case.INSENSITIVE)
                            .findAll();
                    caseRecordList = new CaseRecordList();
                    for (CaseRecord record : realmResults) {
                        caseRecordList.getItems().add(record);
                    }

                } else {
                    RealmResults<CaseRecordList> realmResultsList;
                    realmResultsList = realm.where(CaseRecordList.class).findAll();
                    caseRecordList = realmResultsList.get(0);
                }

                //shown list of records through the adapter
                AdapterRecyclerView adapter;
                if (caseRecordList!=null) {
                    adapter = new AdapterRecyclerView(caseRecordList);
                    //Send a callback to the adapter to update the data
                    adapter.setCallbackForRefresh(mCallback);
                    vRecView.setAdapter(adapter);
                    vRecView.setLayoutManager(new LinearLayoutManager(context));
                }
            }
        });

    }

    private IRecyclerViewListener mCallback = new IRecyclerViewListener() {
        @Override
        public void onPullToRefresh() {
            // callback for refresh
            LinearLayoutManager layoutManager = (LinearLayoutManager) vRecView.getLayoutManager();
            int posFirstViewElem = layoutManager.findFirstCompletelyVisibleItemPosition();
            if (posFirstViewElem <= 0) return;
//            Toast.makeText(getApplicationContext(), "Data update request", Toast.LENGTH_SHORT).show();
            showDataFromServer();
        }

        @Override
        public void onSpinnerGo() {
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSpinnerStop() {
            spinner.setVisibility(View.GONE);
        }
    };
}
