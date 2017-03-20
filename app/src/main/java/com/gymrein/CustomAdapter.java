package com.gymrein;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jcisneros77 on 3/1/17.
 */

public class CustomAdapter extends ArrayAdapter<SideMenuItemModel> implements View.OnClickListener{

    private ArrayList<SideMenuItemModel> dataSet;
    Context menu_context;
    private int item_type = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView tv_item_name;
        ImageView iv_item_image;
    }

    public CustomAdapter(ArrayList<SideMenuItemModel> data, Context context,int layout_type, int itemType) {
        super(context, layout_type, data);

        this.dataSet = data;
        this.menu_context=context;
        this.item_type = itemType;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        SideMenuItemModel SideMenuItemModel=(SideMenuItemModel)object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SideMenuItemModel SideMenuItemModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if (this.item_type == 0) {
                // Side Menu
                convertView = inflater.inflate(R.layout.side_menu_item, parent, false);
                viewHolder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_sm_item_name);
                viewHolder.iv_item_image = (ImageView) convertView.findViewById(R.id.iv_sm_item_image);
            } else if (this.item_type == 1){
                // Credit cards
                convertView = inflater.inflate(R.layout.credit_card_item, parent, false);
                viewHolder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_cc_item_name);
                viewHolder.iv_item_image = (ImageView) convertView.findViewById(R.id.iv_cc_item_image);
            }

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.tv_item_name.setText(SideMenuItemModel.getName());
        viewHolder.iv_item_image.setImageResource(SideMenuItemModel.getImage_path());
        //viewHolder.info.setOnClickListener(this);

        // Return the completed view to render on screen
        return convertView;
    }
}