package com.cezia.recruittest.structure;

import io.realm.RealmObject;

public class CaseRecordAction extends RealmObject {
    public String action;

    public void setAction(String action) {
        this.action = action;
    }
}
