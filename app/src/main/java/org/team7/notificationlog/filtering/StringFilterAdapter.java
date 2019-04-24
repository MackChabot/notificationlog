package org.team7.notificationlog.filtering;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.team7.notificationlog.R;
import org.team7.notificationlog.db.StringFilter;

import java.util.List;

public class StringFilterAdapter extends ArrayAdapter<StringFilter> {

    private static class ViewHolder {
        TextView filterTextView;
    }

    List<StringFilter> sfs;

    public StringFilterAdapter(Context context, List<StringFilter> filters) {
        super(context, -1, filters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StringFilter sfs = getItem(position);

        if (sfs == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.filter_list_item, parent, false);
            return convertView;
        }

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.filter_list_item, parent, false);

            viewHolder.filterTextView = convertView.findViewById(R.id.filterTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.filterTextView.setText(getContext()
                .getResources()
                .getString(R.string.filter_format, sfs.type.equals("Matches") ? "match" : "contain", sfs.filterText, sfs.target));

        return convertView;
    }

    void setFilterData(List<StringFilter> sfs) {
        Log.i("FilterArrayAdapter", "Updating filter data with " + sfs.size() + " filters");
        this.sfs = sfs;

        this.clear();
        this.addAll(sfs);

        this.notifyDataSetChanged();
    }
}