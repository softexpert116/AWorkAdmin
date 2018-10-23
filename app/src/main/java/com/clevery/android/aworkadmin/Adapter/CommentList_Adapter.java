package com.clevery.android.aworkadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.App;
import com.clevery.android.aworkadmin.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentList_Adapter extends BaseAdapter {
    ArrayList<ParseObject> arrayList;
    Context context;

    CommentList_Adapter() {
        arrayList = null;
        context = null;
    }
    public CommentList_Adapter(Context _context, ArrayList<ParseObject> _arrayList) {
        arrayList = _arrayList;
        context = _context;
    }
    @Override
    public int getCount() {

        if (arrayList == null)
            return 0;
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ParseObject data = arrayList.get(i);
        ParseUser user = data.getParseUser("user");
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_comment, null);
        }
        TextView txt_name = (TextView)view.findViewById(R.id.txt_name);
        TextView txt_date = (TextView)view.findViewById(R.id.txt_date);
        TextView txt_description = (TextView)view.findViewById(R.id.txt_description);
        CircleImageView img_user = (CircleImageView)view.findViewById(R.id.img_publisher);
        txt_name.setText(user.getString("fullname"));
        String dateStr = new SimpleDateFormat(App.DATE_FORMAT1).format(data.getCreatedAt());
        txt_date.setText(dateStr);
        txt_description.setText(data.getString("description"));
        Glide.with(context).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_user);

        return view;
    }

}
