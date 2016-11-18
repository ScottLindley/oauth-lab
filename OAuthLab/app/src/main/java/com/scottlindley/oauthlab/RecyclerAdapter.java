package com.scottlindley.oauthlab;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Scott Lindley on 11/17/2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<Tweet> mTweets;

    public RecyclerAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTweet.setText(mTweets.get(position).getText());
        holder.mDate.setText(mTweets.get(position).getCreated_at());
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTweet, mDate;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTweet = (TextView)itemView.findViewById(R.id.tweet_text);
            mDate = (TextView)itemView.findViewById(R.id.tweet_date);
        }
    }
}
