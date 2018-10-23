package com.clevery.android.aworkadmin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.App;
import com.clevery.android.aworkadmin.Fragments.PartyFragment;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.Utils.BlockDialog;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PartyList_Adapter extends BaseAdapter {
    ArrayList<ParseObject> arrayList;
    Context context;
    PartyFragment fragment;

    PartyList_Adapter() {
        context = null;
        arrayList = null;
        fragment = null;
    }
    public PartyList_Adapter(Context _context, ArrayList<ParseObject> _arrayList, PartyFragment _fragment) {
        context = _context;
        arrayList = _arrayList;
        fragment = _fragment;
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
        if (view == null) {
            int resource = R.layout.cell_party;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }
        final ParseObject data = arrayList.get(i);
        Button btn_block = (Button)view.findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BlockDialog blockDialog = new BlockDialog(context);
                blockDialog.onClickListner = new BlockDialog.OnClickListner() {
                    @Override
                    public void OnClickOk() {
                        MainActivity.parseBlockEvent(context, data, blockDialog.abuseType, fragment, null);
                        blockDialog.dismiss();
                    }

                    @Override
                    public void OnClickCancel() {
                        blockDialog.dismiss();
                    }
                };
                blockDialog.show();
            }
        });
        if (fragment == null) {
            btn_block.setVisibility(View.GONE);
        } else {
            btn_block.setVisibility(View.VISIBLE);
        }
        TextView txt_name = (TextView)view.findViewById(R.id.txt_name);
        TextView txt_place = (TextView)view.findViewById(R.id.txt_place);
        TextView txt_invite = (TextView)view.findViewById(R.id.txt_invite);
        TextView txt_type = (TextView)view.findViewById(R.id.txt_type);
        TextView txt_time = (TextView)view.findViewById(R.id.txt_time);
        final CircleImageView img_publisher = (CircleImageView)view.findViewById(R.id.img_publisher);
        final ImageView img_photo = (ImageView)view.findViewById(R.id.img_photo);

        txt_name.setText(data.getString("name"));
        txt_place.setText(data.getString("place"));
        txt_invite.setText("Invites: " + String.valueOf(data.getInt("attend")) + "/"+ String.valueOf(data.getInt("invites")));
        if (data.getInt("attend") == data.getInt("invites")) {
            txt_invite.setTextColor(context.getColor(R.color.colorAccent));
        } else {
            txt_invite.setTextColor(Color.WHITE);
        }

        txt_type.setText(data.getString("type"));
        if (data.getString("type").equals("Free")) {
            txt_type.setBackgroundColor(Color.parseColor("#ff0099cc"));
        } else {
            txt_type.setBackgroundColor(Color.parseColor("#ff669900"));
        }
        String from = new SimpleDateFormat(App.DATE_FORMAT).format(data.getDate("from"));
        String to = new SimpleDateFormat(App.DATE_FORMAT).format(data.getDate("to"));
        txt_time.setText(from + " ~ " + to);
        Glide.with(context).load(data.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.default_pic).centerCrop().dontAnimate()).into(img_photo);
        ParseObject user = data.getParseObject("user");
        Glide.with(context).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_publisher);

        return view;
    }
}
