package com.clevery.android.aworkadmin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.Adapter.PartyList_Adapter;
import com.clevery.android.aworkadmin.Adapter.ReportList_Adapter;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.Model.ReportModel;
import com.clevery.android.aworkadmin.PartyDetailActivity;
import com.clevery.android.aworkadmin.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    MainActivity activity;
    SwipeRefreshLayout refreshLayout;
    ArrayList<ReportModel> arrayList = new ArrayList<>();
    ArrayList<ParseObject> array_media = new ArrayList<>();
    ReportList_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseReportFetch();
            }
        });
        ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ReportModel reportModel = arrayList.get(i);
                if (reportModel.type.equals("Event")) {
                    Intent intent = new Intent(activity, PartyDetailActivity.class);
                    intent.putExtra("OBJECT", reportModel.object);
                    startActivity(intent);
                }
            }
        });
        adapter = new ReportList_Adapter(activity, arrayList, array_media, this);
        listView.setAdapter(adapter);
        return v;
    }
    public void parseReportFetch() {
        refreshLayout.setRefreshing(true);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Report");
        query.include("reporter");
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                refreshLayout.setRefreshing(false);
                if (e == null) {
                    arrayList.clear();
                    if (objects.size() == 0) {
                        activity.parseReportFetch();
                        activity.ly_report.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    configIndex = 0;
                    configArray(objects);
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    int configIndex = 0;
    private void configArray(final List<ParseObject> objects) {
        refreshLayout.setRefreshing(true);
        final ParseObject object = objects.get(configIndex);
        final ReportModel reportModel = new ReportModel();
        reportModel.type = object.getString("type");
        reportModel._id = object.getObjectId();
        reportModel.description = object.getString("description");
        reportModel.reporter = object.getParseUser("reporter");

        ParseQuery<ParseObject> qy = ParseQuery.getQuery(object.getString("type"));
        qy.include("user");
        qy.whereEqualTo("objectId", object.getString("id"));
        qy.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject obj, ParseException e) {
                refreshLayout.setRefreshing(false);
                if (e == null) {
                    reportModel.object = obj;
                    arrayList.add(reportModel);
                } else {
                    try {
                        object.delete();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    object.saveInBackground();
                }
                if (configIndex == objects.size()-1) {
                    activity.parseReportFetch();
                    activity.txt_reports1.setText(String.valueOf(arrayList.size()));
                    if (arrayList.size() == 0) {
                        activity.ly_report.setVisibility(View.GONE);
                    } else {
                        activity.ly_report.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    return;
                } else {
                    configIndex ++;
                    configArray(objects);
                }
            }
        });
    }
    public void parseMediaFetch() {
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Media");
        query1.include("post");
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> medias, ParseException e) {
                if (e == null) {
                    array_media.clear();
                    array_media.addAll(medias);
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        parseMediaFetch();
        parseReportFetch();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
