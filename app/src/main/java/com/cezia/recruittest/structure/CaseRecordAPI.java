package com.cezia.recruittest.structure;

import java.util.List;

public class CaseRecordAPI {
    public String id;
    public String short_name;
    public String full_name;
    public String icon_name;
    public String desc;

    public String client_id;
    public String parent_id;

    public boolean need_confirm;
    public boolean need_note;
    public boolean ignore_parent;
    public boolean is_active;

    public String created_at;
    public String updated_at;

    public List<String> actions;
}
