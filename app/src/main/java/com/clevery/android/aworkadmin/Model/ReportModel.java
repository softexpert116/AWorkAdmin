package com.clevery.android.aworkadmin.Model;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ReportModel {
    public String _id;
    public String type;
    public ParseObject object;
    public ParseUser reporter;
    public String description;

    public ReportModel() {
        this._id = "";
        this.type = "";
        this.object = null;
        this.reporter = null;
        this.description = "";
    }

    public ReportModel(String _id, String type, ParseObject object, ParseUser reporter, String description) {
        this._id = _id;
        this.type = type;
        this.object = object;
        this.reporter = reporter;
        this.description = description;
    }


}
