package com.cezia.recruittest.structure;

import io.realm.RealmList;
import io.realm.RealmObject;

public class CaseRecordList extends RealmObject {
    private RealmList<CaseRecord> items = new RealmList<>();

    public void convertFromAPIList(CaseRecordListAPI list) {
        items.clear();
        for (CaseRecordAPI recordAPI : list.getItems()) {
            CaseRecord caseRecord = new CaseRecord();
            caseRecord.convertFromAPIRecord(recordAPI);
            items.add(caseRecord);
        }
    }

    public RealmList<CaseRecord> getItems() {
        return items;
    }
}
