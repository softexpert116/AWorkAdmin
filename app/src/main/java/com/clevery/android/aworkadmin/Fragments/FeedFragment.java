package com.clevery.android.aworkadmin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.clevery.android.aworkadmin.Adapter.FeedList_Adapter;
import com.clevery.android.aworkadmin.Adapter.PartyList_Adapter;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.PartyDetailActivity;
import com.clevery.android.aworkadmin.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    MainActivity activity;
    SwipeRefreshLayout refreshLayout;
    ArrayList<ParseObject> arrayList = new ArrayList<>();
    ArrayList<ParseObject> array_filter = new ArrayList<>();
    ArrayList<ParseObject> array_media = new ArrayList<>();
    ArrayList<ParseObject> array_comment = new ArrayList<>();

    FeedList_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parsePostFetch();
            }
        });
        ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, PartyDetailActivity.class);
                startActivity(intent);

            }
        });
        adapter = new FeedList_Adapter(activity, array_filter, array_media, array_comment, this);
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
                    filterFeedArray(edit_search.getText().toString().trim());
                }
                adapter.notifyDataSetChanged();
            }
        });
        parsePostFetch();
        return v;
    }
    private void filterFeedArray(String key) {
        array_filter.clear();
        for (ParseObject object:arrayList) {
            if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                continue;
            }
            if (object.getString("description").toLowerCase().contains(key.toLowerCase())) {
                array_filter.add(object);
            }
        }
    }
    public void parsePostFetch() {
        refreshLayout.setRefreshing(true);
        ParseQuery<ParseObject> query0 = ParseQuery.getQuery("Post");
        query0.include("user");
        query0.orderByDescending("createdAt");
        query0.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {
                if (e == null) {
                    arrayList.clear();
                    arrayList.addAll(posts);
                    array_filter.clear();
                    array_filter.addAll(posts);
                    if (posts.size() == 0) {
                        adapter.notifyDataSetChanged();
                        refreshLayout.setRefreshing(false);
                        return;
                    }

                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Media");
                    query1.include("post");
                    query1.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> medias, ParseException e) {
                            if (e == null) {
                                array_media.clear();
                                array_media.addAll(medias);
                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Comment");
                                query2.include("user");
                                query2.orderByDescending("createdAt");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> comments, ParseException e) {
                                        if (e == null) {
                                            array_comment.clear();
                                            array_comment.addAll(comments);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        refreshLayout.setRefreshing(false);
                                    }
                                });
                            } else {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                                refreshLayout.setRefreshing(false);
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
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
