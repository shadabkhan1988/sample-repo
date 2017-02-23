package com.rahulhooda.sampleweatherapp.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahulhooda.sampleweatherapp.Model.NewsDetail;
import com.rahulhooda.sampleweatherapp.R;

/**
 * Created by rahul.hooda on 2/17/17.
 */

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NewsDetail mNewsDetail;
    private int WEATHER_VIEW = 0;
    private int mListSize;
    private View mWeatherView;

    public CustomAdapter(NewsDetail newsDetail, int listSize, int weatherAdPosition, View weatherView) {
        mNewsDetail = newsDetail;
        mListSize = listSize;
        WEATHER_VIEW = weatherAdPosition;
        mWeatherView = weatherView;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == WEATHER_VIEW)
            return WEATHER_VIEW;
        else
            return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if ((viewType == WEATHER_VIEW) && (mWeatherView != null))
            viewHolder = new WeatherAdViewHolder(mWeatherView);
        else {
            View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
            viewHolder = new MyViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == WEATHER_VIEW && mWeatherView != null) {
            // bind the required data
            WeatherAdViewHolder viewHolder = (WeatherAdViewHolder) holder;
            configureWeatherViewHolder(viewHolder, position);
        } else {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            configureMyViewHolder(viewHolder, position);
        }
    }

    /**
     * This methods is used for do required bindings for MyViewHolder.
     */
    private void configureMyViewHolder(MyViewHolder holder, int position) {
        holder.newsTitle.setText(mNewsDetail.getNewsTitle());
        holder.newsDescription.setText(mNewsDetail.getNewDescription());
        holder.newsDateAndTime.setText(mNewsDetail.getNewsDateAndTime());
        holder.newsReporter.setText(mNewsDetail.getNewsReporter());
    }

    /**
     * This methods is used for do required bindings for WeatherAdViewHolder.
     */
    private void configureWeatherViewHolder(WeatherAdViewHolder holder, int position) {
        //holder.textView.setText("This is a hetrogeneous List");
    }

    @Override
    public int getItemCount() {
        return mListSize;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView newsTitle, newsDescription, newsDateAndTime, newsReporter;
        public ImageView newsImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsDescription = (TextView) itemView.findViewById(R.id.news_description);
            newsDateAndTime = (TextView) itemView.findViewById(R.id.news_date_and_time);
            newsReporter = (TextView) itemView.findViewById(R.id.news_reporter);
            newsImage = (ImageView) itemView.findViewById(R.id.news_image);
        }
    }

    public class WeatherAdViewHolder extends RecyclerView.ViewHolder {

        public WeatherAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}
