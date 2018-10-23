package com.clevery.android.aworkadmin.Fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.Adapter.PartyList_Adapter;
import com.clevery.android.aworkadmin.Adapter.UserList_Adapter;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.PartyDetailActivity;
import com.clevery.android.aworkadmin.R;
import com.felipecsl.quickreturn.library.AbsListViewQuickReturnAttacher;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PartyFragment extends Fragment {
    MainActivity activity;
    SwipeRefreshLayout refreshLayout;
    ArrayList<ParseObject> arrayList = new ArrayList<>();
    ArrayList<ParseObject> array_filter = new ArrayList<>();
    PartyList_Adapter adapter;
    ListView listView;
    RelativeLayout ly_search;
//    private QuickReturnAttacher quickReturnAttacher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_party, container, false);
        ly_search = (RelativeLayout)v.findViewById(R.id.ly_search);
        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseEventFetch();
            }
        });
        listView = (ListView)v.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, PartyDetailActivity.class);
                intent.putExtra("OBJECT", array_filter.get(i));
                startActivity(intent);

            }
        });
        adapter = new PartyList_Adapter(activity, array_filter, this);
        listView.setAdapter(adapter);


        final EditText edit_search = (EditText)v.findViewById(R.id.edit_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edit_search.getText().toString().length() == 0) {
                    array_filter.clear();
                    array_filter.addAll(arrayList);
                } else {
                    filterPartyArray(edit_search.getText().toString().trim());
                }
                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }
    private void filterPartyArray(String key) {
        array_filter.clear();
        for (ParseObject object:arrayList) {
            if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                continue;
            }
            if (object.getString("name").toLowerCase().contains(key.toLowerCase())) {
                array_filter.add(object);
            }
        }
    }
    public void parseEventFetch() {
        refreshLayout.setRefreshing(true);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.include("user");
        query.orderByAscending("from");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    arrayList.clear();
                    arrayList.addAll(objects);
                    array_filter.clear();
                    array_filter.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        parseEventFetch();

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
