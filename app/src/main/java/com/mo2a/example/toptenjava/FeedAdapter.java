package com.mo2a.example.toptenjava;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter<T extends FeedEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> entries;

    FeedAdapter(Context context, int resource, List<FeedEntry> entries) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FeedEntry currentEntry= entries.get(position);
        if (currentEntry.getTitle() == null) {
            viewHolder.tvTitle.setVisibility(View.GONE);
        } else{
            viewHolder.tvTitle.setText(currentEntry.getTitle());
        }
        if (currentEntry.getArtist() == null) {
            viewHolder.tvArtist.setVisibility(View.GONE);
        } else{
            viewHolder.tvArtist.setText(currentEntry.getArtist());
        }
        if (currentEntry.getName() == null) {
            viewHolder.tvName.setVisibility(View.GONE);
        } else{
            viewHolder.tvName.setText(currentEntry.getName());
        }
        if (currentEntry.getSummary() == null) {
            viewHolder.tvSummary.setVisibility(View.GONE);
        } else{
            viewHolder.tvSummary.setText(currentEntry.getSummary());
        }


        return convertView;
    }

    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;
        final  TextView tvTitle;


        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
            this.tvTitle= v.findViewById(R.id.feedTitle);
        }
    }
}
