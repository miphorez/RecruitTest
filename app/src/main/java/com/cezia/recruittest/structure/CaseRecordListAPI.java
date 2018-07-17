package com.cezia.recruittest.structure;

import java.util.ArrayList;
import java.util.List;

public class CaseRecordListAPI {
    private ArrayList<CaseRecordAPI> items = new ArrayList<>();

    public CaseRecordListAPI(List<CaseRecordAPI> list) {
        items.addAll(list);
    }

    public ArrayList<CaseRecordAPI> getItems() {
        return items;
    }
}
