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

import com.clevery.android.aworkadmin.Adapter.UserList_Adapter;
import com.clevery.android.aworkadmin.LoginActivity;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.UserProfileActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    MainActivity activity;
    SwipeRefreshLayout refreshLayout;
    ArrayList<ParseUser> arrayList = new ArrayList<>();
    ArrayList<ParseObject> arrayBlock = new ArrayList<>();
    ArrayList<ParseUser> array_filter = new ArrayList<>();
    UserList_Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        refreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                parseUserFetch();
            }
        });
        final ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, UserProfileActivity.class);
                intent.putExtra("USER", array_filter.get(i));
                startActivity(intent);
            }
        });
        adapter = new UserList_Adapter(activity, array_filter, arrayBlock, this);
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
                    filterUserArray(edit_search.getText().toString().trim());
                }
                adapter.notifyDataSetChanged();
            }
        });
        parseUserFetch();
        return v;
    }
    private void filterUserArray(String key) {
        array_filter.clear();
        for (ParseUser user:arrayList) {
            if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                continue;
            }
            if (user.getString("fullname").toLowerCase().contains(key.toLowerCase())) {
                array_filter.add(user);
            }
        }
    }
    public void parseUserFetch() {
        refreshLayout.setRefreshing(true);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("emailVerified", true);
        query.whereEqualTo("isAdmin", false);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                refreshLayout.setRefreshing(false);
                if (e == null) {
                    arrayList.clear();
                    arrayList.addAll(objects);
                    array_filter.clear();
                    array_filter.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void parseBlockFetch() {
        refreshLayout.setRefreshing(true);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Block");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                refreshLayout.setRefreshing(false);
                arrayBlock.clear();
                if (e == null) {
                    for (ParseObject object:objects)  {
                        if (object.getCreatedAt().getTime()/1000 + 24*3600*10 < System.currentTimeMillis()/1000) {
                            try {
                                object.delete();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            object.saveInBackground();
                            objects.remove(object);
                        }
                    }
                    arrayBlock.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        parseBlockFetch();
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
