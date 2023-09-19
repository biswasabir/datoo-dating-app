package com.angopapo.datooapp.adapters.datoo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.angopapo.datooapp.R;
import com.angopapo.datooapp.home.live.LiveStreamingActivity;
import com.angopapo.datooapp.models.datoo.GiftModel;

import java.util.List;

public class GiftLiveFooterAdapter extends RecyclerView.Adapter<GiftLiveFooterAdapter.ViewHolder> {
    private List<GiftModel> mGifts;
    private Activity mActivity;


    public GiftLiveFooterAdapter(Activity activity, List<GiftModel> giftModelList) {
        mGifts = giftModelList;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view;

        view = inflater.inflate(R.layout.footer_item_live_gift_large, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GiftModel gift = mGifts.get(position);

        viewHolder.mGiftImage.setAnimationFromUrl(gift.getGiftFile().getUrl());
        viewHolder.mGiftImage.setSpeed(0.8f);

        viewHolder.mGiftCredits.setText(String.valueOf(gift.getCredits()));

        viewHolder.mLayout.setOnClickListener(v -> ((LiveStreamingActivity) mActivity).sendLiveGift(gift));
    }

    @Override
    public int getItemCount() {
        return mGifts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mLayout;
        LottieAnimationView mGiftImage;
        TextView mGiftCredits;

        ViewHolder(View v) {
            super(v);

            mLayout = v.findViewById(R.id.root_view);
            mGiftImage = v.findViewById(R.id.itemLiveGift_iconImage);
            mGiftCredits = v.findViewById(R.id.itemLiveGift_priceText);
        }
    }

}