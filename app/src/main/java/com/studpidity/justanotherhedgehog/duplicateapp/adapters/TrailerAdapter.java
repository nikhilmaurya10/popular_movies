package com.studpidity.justanotherhedgehog.duplicateapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import com.squareup.picasso.Picasso;

import com.studpidity.justanotherhedgehog.duplicateapp.R;
import com.studpidity.justanotherhedgehog.duplicateapp.model.TrailerItem;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String LOG_TAG = TrailerAdapter.class.getSimpleName();
    private final String youtubeThumbnailBaseUrl = "http://img.youtube.com/vi/";
    private final String youtubeThumbnailBaseUrlPostfix = "/0.jpg";
    private final ArrayList<TrailerItem> mTrailers;
    private final Callbacks mCallbacks;

    public interface Callbacks {
        void watch(TrailerItem trailer, int position);
    }

    public TrailerAdapter(ArrayList<TrailerItem> trailers, Callbacks callbacks) {
        mTrailers = trailers;
        mCallbacks = callbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrailerItem trailer = mTrailers.get(position);
        final Context context = holder.mView.getContext();
        float paddingLeft = 0;
        if (position == 0) {
            paddingLeft = 8;
        }
        float paddingRight = 0;
        if (position + 1 != getItemCount()) {
            paddingRight = 8;
        }
        holder.mView.setPadding((int) paddingLeft, 0, (int) paddingRight, 0);
        holder.mTrailer = trailer;
        String thumbnailUrl = youtubeThumbnailBaseUrl + trailer.getKey() + youtubeThumbnailBaseUrlPostfix;
        Picasso.with(context)
                .load(thumbnailUrl)
                .placeholder(R.drawable.placeholder_light)
                .error(R.drawable.error_placeholder)
                .into(holder.mThumbnailView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.watch(trailer, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        @BindView(R.id.trailer_thumbnail) ImageView mThumbnailView;
        public TrailerItem mTrailer;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            mView = view;
        }
    }

    public void add(ArrayList<TrailerItem> trailers) {
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }
}

