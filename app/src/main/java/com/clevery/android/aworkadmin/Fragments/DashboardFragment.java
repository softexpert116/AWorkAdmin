package com.clevery.android.aworkadmin.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.Utils.Utils;
import com.jonas.jgraph.graph.NChart;
import com.jonas.jgraph.models.NExcel;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment {
    MainActivity activity;
    NChart chart_user, chart_party, chart_feed;
    Button btn_year_user, btn_year_party, btn_year_feed;
    Button btn_erase_user, btn_erase_party, btn_erase_feed;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        btn_erase_user = (Button)v.findViewById(R.id.btn_erase_user);
        btn_erase_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you going to clear the data?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        parseStatisticsErase("User", Integer.valueOf(btn_year_user.getText().toString()));
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        btn_erase_party = (Button)v.findViewById(R.id.btn_erase_party);
        btn_erase_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you going to clear the data?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        parseStatisticsErase("Event", Integer.valueOf(btn_year_party.getText().toString()));
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        btn_erase_feed = (Button)v.findViewById(R.id.btn_erase_feed);
        btn_erase_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you going to clear the data?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        parseStatisticsErase("Post", Integer.valueOf(btn_year_feed.getText().toString()));
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        btn_year_user = (Button)v.findViewById(R.id.btn_year_user);
        btn_year_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final com.clevery.android.aworkadmin.Utils.NumberPicker numberPicker = new com.clevery.android.aworkadmin.Utils.NumberPicker(activity, 2015, 2050, "Please select year");
                numberPicker.onClickListner = new com.clevery.android.aworkadmin.Utils.NumberPicker.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        int year = numberPicker.selectedValue;
                        onResume();
                        parseStatisticsFetch("User", year);
                        btn_year_user.setText(String.valueOf(year));
                        numberPicker.dismiss();
                    }

                    @Override
                    public void OnClickCancel() {
                        numberPicker.dismiss();
                    }
                };
            }
        });

        btn_year_party = (Button)v.findViewById(R.id.btn_year_party);
        btn_year_party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final com.clevery.android.aworkadmin.Utils.NumberPicker numberPicker = new com.clevery.android.aworkadmin.Utils.NumberPicker(activity, 2015, 2050, "Please select year");
                numberPicker.onClickListner = new com.clevery.android.aworkadmin.Utils.NumberPicker.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        int year = numberPicker.selectedValue;
                        parseStatisticsFetch("Event", year);
                        btn_year_party.setText(String.valueOf(year));
                        numberPicker.dismiss();
                    }

                    @Override
                    public void OnClickCancel() {
                        numberPicker.dismiss();
                    }
                };
            }
        });
        btn_year_feed = (Button)v.findViewById(R.id.btn_year_feed);
        btn_year_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final com.clevery.android.aworkadmin.Utils.NumberPicker numberPicker = new com.clevery.android.aworkadmin.Utils.NumberPicker(activity, 2015, 2050, "Please select year");
                numberPicker.onClickListner = new com.clevery.android.aworkadmin.Utils.NumberPicker.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        int year = numberPicker.selectedValue;
                        parseStatisticsFetch("Post", year);
                        btn_year_feed.setText(String.valueOf(year));
                        numberPicker.dismiss();
                    }

                    @Override
                    public void OnClickCancel() {
                        numberPicker.dismiss();
                    }
                };
            }
        });
        chart_user = (NChart) v.findViewById(R.id.chart_user);
        chart_party = (NChart) v.findViewById(R.id.chart_party);
        chart_feed = (NChart) v.findViewById(R.id.chart_feed);
        return v;
    }
    void initChart(String type, List<NExcel> data) {
        String colorStr = "#ff0000";
        NChart chartView = chart_user;
        if (type.equals("User")) {
            colorStr = "#ff0000";
            chartView = chart_user;
            if (isEmptyData(data)) {
                btn_erase_user.setEnabled(false);
                btn_erase_user.setBackgroundResource(R.drawable.ic_erase1);
            } else {
                btn_erase_user.setEnabled(true);
                btn_erase_user.setBackgroundResource(R.drawable.ic_erase);
            }
        } else if (type.equals("Event")) {
            colorStr = "#00ff00";
            chartView = chart_party;
            if (isEmptyData(data)) {
                btn_erase_party.setEnabled(false);
                btn_erase_party.setBackgroundResource(R.drawable.ic_erase1);
            } else {
                btn_erase_party.setEnabled(true);
                btn_erase_party.setBackgroundResource(R.drawable.ic_erase);
            }
        } else if (type.equals("Post")) {
            colorStr = "#0000ff";
            chartView = chart_feed;
            if (isEmptyData(data)) {
                btn_erase_feed.setEnabled(false);
                btn_erase_feed.setBackgroundResource(R.drawable.ic_erase1);
            } else {
                btn_erase_feed.setEnabled(true);
                btn_erase_feed.setBackgroundResource(R.drawable.ic_erase);
            }
        }
        chartView.setBarWidth(25);
        chartView.setInterval(5);
        chartView.setAbove(0);
        chartView.setSelectedModed(NChart.SelectedMode.selecetdMsgShow);
        chartView.setBarStanded(7);
        chartView.setNormalColor(Color.parseColor(colorStr));
        chartView.cmdFill(data);
    }
    private boolean isEmptyData(List<NExcel> data) {
        for (NExcel nExcel:data) {
            if (nExcel.getHeight() > 0) {
                return false;
            }
        }
        return true;
    }
    private void parseStatisticsFetch(final String type, int year) {
        progressBar.setVisibility(View.VISIBLE);
        final List<NExcel> list;
        list = initGraphData();
        ParseQuery<ParseObject> query0 = ParseQuery.getQuery("Statistics");
        query0.whereEqualTo("type", type);
        query0.whereEqualTo("year", year);
        query0.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> statistics, ParseException e) {
                if (e == null) {
                    for (ParseObject statistic:statistics) {
                        int month = statistic.getNumber("month").intValue();
                        int count = statistic.getNumber("count").intValue();
                        updateGraphData(list, month, count);
                    }
                    initChart(type, list);
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void parseStatisticsErase(final String type, int year) {
        final List<NExcel> list;
        list = initGraphData();
        ParseQuery<ParseObject> query0 = ParseQuery.getQuery("Statistics");
        query0.whereEqualTo("type", type);
        query0.whereEqualTo("year", year);
        query0.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> statistics, ParseException e) {
                if (e == null) {
                    for (ParseObject statistic:statistics) {
                        try {
                            statistic.delete();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        statistic.saveInBackground();
                    }
                    initChart(type, list);
                } else {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private List<NExcel> initGraphData() {
        List<NExcel> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(new NExcel(0, String.valueOf(i+1)));
        }
        return list;
    }
    private void updateGraphData(List<NExcel> data, int month, int count) {
        for (NExcel nExcel:data) {
            if (nExcel.getXmsg().equals(String.valueOf(month))) {
                nExcel.setNum(count);
                nExcel.setHeight(count);
                nExcel.setUpper(count);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int cur_year = Utils.getYear(Calendar.getInstance().getTime());
        btn_year_feed.setText(String.valueOf(cur_year));
        btn_year_user.setText(String.valueOf(cur_year));
        btn_year_party.setText(String.valueOf(cur_year));
        parseStatisticsFetch("User", cur_year);
        parseStatisticsFetch("Event", cur_year);
        parseStatisticsFetch("Post", cur_year);
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
