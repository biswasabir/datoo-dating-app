package com.angopapo.datooapp.adapters.others;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.angopapo.datooapp.R;
import com.angopapo.datooapp.app.Config;
import com.angopapo.datooapp.helpers.QuickHelp;
import com.angopapo.datooapp.home.settings.WebUrlsActivity;

import java.util.List;


public class PaymentProductListSkuAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Activity mActivity;
    private final ItemViewHolder.OnItemSelectedListener itemSelectedListener;

    private final List<SkuDetails> mPaymentProductModels;

    private static int lastSelectedPosition = 0;

    private static final int NORMAL_ITEM = 0;
    private static final int FOOTER_VIEW = 1;

    public PaymentProductListSkuAdapterNew(Activity activity, ItemViewHolder.OnItemSelectedListener itemSelectedListener, List<SkuDetails> paymentProductModelList) {
        this.mActivity = activity;
        this.itemSelectedListener = itemSelectedListener;
        this.mPaymentProductModels = paymentProductModelList;
    }

    private boolean isHeader(int position) {
        return position == mPaymentProductModels.size() -1;
    }

    @Override
    public int getItemViewType(int position) {

        //return isHeader(position) ? FOOTER_VIEW : NORMAL_ITEM;
        return NORMAL_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.payment_product_package_grid_item, parent, false);
        return new ItemViewHolder(view, itemSelectedListener);

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        SkuDetails productModel = mPaymentProductModels.get(position);

        if (viewHolder.getItemViewType() == NORMAL_ITEM){

            ItemViewHolder holder = ((ItemViewHolder) viewHolder);

            if (lastSelectedPosition == position){
                holder.mItemLayout.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.payments_grid_selected_border));
                holder.mSelectedIcon.setVisibility(View.VISIBLE);
            } else {
                holder.mItemLayout.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.payments_grid_unselected_border));
                holder.mSelectedIcon.setVisibility(View.INVISIBLE);
            }

            holder.savePercent.setPaintFlags(holder.savePercent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (productModel.getType().equals(BillingClient.SkuType.INAPP)){

                holder.ItemUnit.setText(mActivity.getString(R.string.credits_));

                switch (productModel.getSku()) {
                    case Config.CREDIT_550:

                        holder.ItemName.setText(mActivity.getString(R.string.credits_550_));
                        holder.ItemPrice.setText(productModel.getPrice()); // 12,99
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(38.5, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode())); // 38.5% = 18$
                        holder.mostPopularText.setVisibility(View.VISIBLE);
                        holder.mostPopularText.setText(mActivity.getString(R.string.Payments_Most_Popular));

                        break;
                    case Config.CREDIT_100:

                        holder.ItemName.setText(mActivity.getString(R.string.credits_100_));
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(33.7, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode())); // 33.7%
                        holder.ItemPrice.setText(productModel.getPrice()); // 2,99$
                        holder.mostPopularText.setVisibility(View.INVISIBLE);

                        break;
                    case Config.CREDIT_1250:

                        holder.ItemName.setText(mActivity.getString(R.string.credits_1250_));
                        holder.ItemPrice.setText(productModel.getPrice()); // 19,99$
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(85, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode())); // 85%
                        holder.mostPopularText.setVisibility(View.INVISIBLE);

                        break;
                    case Config.CREDIT_2750:

                        holder.ItemName.setText(mActivity.getString(R.string.credits_2750_));
                        holder.ItemPrice.setText(productModel.getPrice()); // 39,99$
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(80, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode())); // 80%
                        holder.mostPopularText.setVisibility(View.VISIBLE);
                        holder.mostPopularText.setText(mActivity.getString(R.string.payments_best_value));

                        break;
                }

            } else if (productModel.getType().equals(BillingClient.SkuType.SUBS)){

                switch (productModel.getSku()) {
                    case Config.SUBS_3_MONTHS:

                        holder.ItemName.setText(mActivity.getString(R.string.subs_3_months_));
                        holder.ItemUnit.setText(mActivity.getString(R.string.months));
                        holder.ItemPrice.setText(productModel.getPrice()); // 43,99$
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(256.8, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode()));
                        holder.mostPopularText.setVisibility(View.VISIBLE);
                        holder.mostPopularText.setText(mActivity.getString(R.string.Payments_Most_Popular));

                        break;
                    case Config.SUBS_1_WEEK:

                        holder.ItemName.setText(mActivity.getString(R.string.subs_1_week_));
                        holder.ItemPrice.setText(productModel.getPrice()); // 5,99$
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(167.1, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode()));
                        holder.ItemUnit.setText(mActivity.getString(R.string.week));
                        holder.mostPopularText.setVisibility(View.INVISIBLE);

                        break;
                    case Config.SUBS_1_MONTH:

                        holder.ItemName.setText(mActivity.getString(R.string.subs_1_month_)); // 16,99$
                        holder.ItemUnit.setText(mActivity.getString(R.string.month));
                        holder.ItemPrice.setText(productModel.getPrice());
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(247.2, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode()));
                        holder.mostPopularText.setVisibility(View.INVISIBLE);

                        break;
                    case Config.SUBS_6_MONTHS:

                        holder.ItemName.setText(mActivity.getString(R.string.subs_6_months_)); // 65,99$
                        holder.ItemUnit.setText(mActivity.getString(R.string.months));
                        holder.ItemPrice.setText(productModel.getPrice());
                        holder.savePercent.setText(QuickHelp.getPercentageOfWithCurrency(327.3, productModel.getPriceAmountMicros(), productModel.getPriceCurrencyCode()));
                        holder.mostPopularText.setVisibility(View.VISIBLE);
                        holder.mostPopularText.setText(mActivity.getString(R.string.payments_best_value));

                        break;
                }
            }

            holder.mItem = productModel;
        }

    }

    @Override
    public int getItemCount() {
        return mPaymentProductModels.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mTermsnAndConditions;

        HeaderViewHolder(@NonNull View v) {
            super(v);

            mTermsnAndConditions = v.findViewById(R.id.terms);
        }
    }

    public void setSelected(SkuDetails paymentProductModel){

        itemSelectedListener.onItemSelected(paymentProductModel);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        SkuDetails mItem;
        OnItemSelectedListener itemSelectedListener;

        RelativeLayout mItemLayout;

        TextView ItemName; // 550 / 1
        TextView ItemUnit; // credits / Month
        TextView ItemPrice; // 10 $
        TextView savePercent;
        TextView mostPopularText; // Popular

        ImageView mSelectedIcon;

        ItemViewHolder(View v, OnItemSelectedListener itemSelected) {
            super(v);

            itemSelectedListener = itemSelected;

            mItemLayout = v.findViewById(R.id.productListItem_paymentBox);
            ItemName = v.findViewById(R.id.payment_product_grid_unit);
            ItemUnit = v.findViewById(R.id.payment_product_grid_name);
            ItemPrice = v.findViewById(R.id.payment_product_grid_price);
            mostPopularText = v.findViewById(R.id.payment_product_grid_popular_label);
            mSelectedIcon = v.findViewById(R.id.payment_product_grid_selected_icon);
            savePercent = v.findViewById(R.id.payment_product_grid_price_old);

            mItemLayout.setOnClickListener(view -> {

                lastSelectedPosition = getBindingAdapterPosition();
                itemSelectedListener.onItemSelected(mItem);
            });
        }

        public interface OnItemSelectedListener {

            void onItemSelected(SkuDetails item);
        }
    }

}