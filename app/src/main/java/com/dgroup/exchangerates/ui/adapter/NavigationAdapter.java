package com.dgroup.exchangerates.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dgroup.exchangerates.R;


public class NavigationAdapter extends BaseAdapter{

    private String[] items;
    private int[] resIds;

    public NavigationAdapter(String[] items, int[] resIds) {
        this.items = items;
        this.resIds = resIds;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_navigation, parent, false);
        }
        Log.i("NavigationAdapter","getView "+position+" ((ListView)parent).getCheckedItemPosition() "+((ListView)parent).getCheckedItemPosition());
        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        name.setText(items[position]);
        name.setSelected(((ListView)parent).getCheckedItemPosition() == position);

        imageView.setImageResource(resIds[position]);
        return convertView;
    }
}
