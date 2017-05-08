package com.gymrein;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jcisneros77 on 4/10/17.
 */

public class ClassItemAdapter extends ArrayAdapter<ClassItemModel> implements View.OnClickListener {
    private ArrayList<ClassItemModel> dataSet;
    Context menu_context;
    private boolean pastFlag;

    // View lookup cache
    private static class ViewHolder {
        TextView tv_item_name;
        TextView tv_item_time;
        ImageView iv_item_image;
        TextView tv_item_date;
        TextView tv_item_location;
    }

    public ClassItemAdapter(ArrayList<ClassItemModel> data, Context context,int layout_type,boolean past) {
        super(context, layout_type, data);

        this.dataSet = data;
        this.menu_context=context;
        this.pastFlag = past;

    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        ClassItemModel ClassItemModel=(ClassItemModel) object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ClassItemModel ClassItemModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.class_of_day_item, parent, false);
            if (pastFlag) {
                if (!ClassItemModel.getAssisted())
                    convertView.setBackgroundColor(Color.RED);
            }
            viewHolder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_cod_item_name);
            viewHolder.tv_item_time = (TextView) convertView.findViewById(R.id.tv_cod_item_time);
            viewHolder.iv_item_image = (ImageView) convertView.findViewById(R.id.iv_cod_item_image);
            viewHolder.tv_item_date = (TextView) convertView.findViewById(R.id.tv_cod_item_date);
            viewHolder.tv_item_location = (TextView) convertView.findViewById(R.id.tv_cod_item_location);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.tv_item_name.setText(ClassItemModel.getEvent_name());
        viewHolder.tv_item_location.setText(ClassItemModel.getLocation_name());

        try {
            SimpleDateFormat toFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = ClassItemModel.getDate().replace("T"," ").replace("Z","");
            //System.out.println("DATE: " + dateStr);
            Date fullDate = toFullDate.parse(dateStr);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String fDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(fullDate);

            String shortTimeStr = sdf.format(fullDate);
           //System.out.println("TIME: " + shortTimeStr);
            viewHolder.tv_item_time.setText(shortTimeStr);
            viewHolder.tv_item_date.setText(fDate);
        } catch (ParseException e){
            System.out.println(e);
        }

        Picasso.with(menu_context).load(ClassItemModel.getLogo_url()).into(viewHolder.iv_item_image);
        return convertView;
    }

}
