package com.clevery.android.aworkadmin.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.aworkadmin.App;
import com.clevery.android.aworkadmin.Fragments.UserFragment;
import com.clevery.android.aworkadmin.MainActivity;
import com.clevery.android.aworkadmin.R;
import com.clevery.android.aworkadmin.Utils.BlockDialog;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserList_Adapter extends BaseAdapter {
    ArrayList<ParseUser> arrayList;
    ArrayList<ParseObject> arrayBlock;
    Context context;
    UserFragment fragment;

    UserList_Adapter() {
        context = null;
        arrayList = null;
        fragment = null;
    }
    public UserList_Adapter(Context _context, ArrayList<ParseUser> _arrayList, ArrayList<ParseObject> _arrayBlock, UserFragment _fragment) {
        context = _context;
        arrayList = _arrayList;
        arrayBlock = _arrayBlock;
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
        final ParseUser user = arrayList.get(i);
        if (view == null) {
            int resource = R.layout.cell_user;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }
        TextView txt_name = (TextView)view.findViewById(R.id.txt_name);
        TextView txt_email = (TextView)view.findViewById(R.id.txt_email);
        CircleImageView img_user = (CircleImageView)view.findViewById(R.id.img_user);
        txt_name.setText(user.getString("fullname"));
        txt_email.setText(user.getString("username"));
        Glide.with(context).load(user.getParseFile("photo").getUrl()).apply(new RequestOptions()
                .placeholder(R.drawable.ic_user).centerCrop().dontAnimate()).into(img_user);

        final Button btn_block = (Button)view.findViewById(R.id.btn_block);
        btn_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_block.getText().toString().equals("Block")) {
                    final BlockDialog blockDialog = new BlockDialog(context);
                    blockDialog.onClickListner = new BlockDialog.OnClickListner() {
                        @Override
                        public void OnClickOk() {
                            MainActivity.parseBlockUser(context, user, blockDialog.abuseType, fragment);
                            blockDialog.dismiss();
                        }

                        @Override
                        public void OnClickCancel() {
                            blockDialog.dismiss();
                        }
                    };
                    blockDialog.show();
                } else {
                    MainActivity.parseUnBlockUser(context, user, fragment);
                }
            }
        });

        boolean block_flag = false;
        for (ParseObject object: arrayBlock) {
            if (object.getParseUser("user").getObjectId().equals(user.getObjectId())) {
                block_flag = true;
                break;
            }
        }
        if (block_flag) {
            btn_block.setText("Unblock");
            btn_block.setTextColor(context.getColor(R.color.colorButton));
        } else {
            btn_block.setText("Block");
            btn_block.setTextColor(Color.parseColor("#ff0000"));
        }
        return view;
    }
}
