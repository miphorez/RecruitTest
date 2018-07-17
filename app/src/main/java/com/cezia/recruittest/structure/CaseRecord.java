package com.cezia.recruittest.structure;

import io.realm.RealmList;
import io.realm.RealmObject;

public class CaseRecord extends RealmObject {
    private String id;
    private String short_name;
    private String full_name;
    private String icon_name;
    private String desc;

    private String client_id;
    private String parent_id;

    private boolean need_confirm;
    private boolean need_note;
    private boolean ignore_parent;
    private boolean is_active;

    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public boolean isNeed_confirm() {
        return need_confirm;
    }

    public void setNeed_confirm(boolean need_confirm) {
        this.need_confirm = need_confirm;
    }

    public boolean isNeed_note() {
        return need_note;
    }

    public void setNeed_note(boolean need_note) {
        this.need_note = need_note;
    }

    public boolean isIgnore_parent() {
        return ignore_parent;
    }

    public void setIgnore_parent(boolean ignore_parent) {
        this.ignore_parent = ignore_parent;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public RealmList<CaseRecordAction> getActions() {
        return actions;
    }

    public void setActions(RealmList<CaseRecordAction> actions) {
        this.actions = actions;
    }

    private RealmList<CaseRecordAction> actions = new RealmList<>();

    public void convertFromAPIRecord(CaseRecordAPI recordAPI) {
        id = recordAPI.id;
        short_name = recordAPI.short_name;
        full_name = recordAPI.full_name;
        icon_name = recordAPI.icon_name;
        desc = recordAPI.desc;
        client_id = recordAPI.client_id;
        parent_id = recordAPI.parent_id;
        need_confirm = recordAPI.need_confirm;
        need_note = recordAPI.need_note;
        ignore_parent = recordAPI.ignore_parent;
        is_active = recordAPI.is_active;
        created_at = recordAPI.created_at;
        updated_at = recordAPI.updated_at;

        actions.clear();
        for(String str : recordAPI.actions) {
            CaseRecordAction newAction = new CaseRecordAction();
            newAction.setAction(str);
            actions.add(newAction);
        }
    }
}
