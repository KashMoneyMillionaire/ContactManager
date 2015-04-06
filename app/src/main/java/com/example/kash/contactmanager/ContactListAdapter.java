package com.example.kash.contactmanager;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kash on 3/10/2015.
 * The adapter allows communication from the view to the data and events underneath
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    public List<Contact> mDataset;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public View mContactView;
        public TextView mContactName;
        public TextView mContactPhone;
        public TextView mContactEmail;
        public LinearLayout mHiddenLayout;
        public TextView mItemPosition;

        private int mOriginalHeight = 0;
        private boolean mIsViewExpanded = false;

        public ViewHolder(View v) {
            super(v);
            mContactView = v;
            mContactName = (TextView) mContactView.findViewById(R.id.contact_name);
            mContactPhone = (TextView) mContactView.findViewById(R.id.contact_phone);
            mContactEmail = (TextView) mContactView.findViewById(R.id.contact_email);
            mHiddenLayout = (LinearLayout) mContactView.findViewById(R.id.hidden_info);
            mItemPosition = (TextView) mContactView.findViewById(R.id.item_position);

            v.setOnClickListener(this);
        }

        //this changes the size of the visible space for each item on click.
        //it also displays te buttons for editing/deleating
        @Override
        public void onClick(final View view) {
            if (mOriginalHeight == 0) {
                mOriginalHeight = view.getHeight();
            }
            ValueAnimator valueAnimator;
            if (!mIsViewExpanded) {
                mIsViewExpanded = true;
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight, mOriginalHeight + (int) (mOriginalHeight * 1));
                mHiddenLayout.setVisibility(View.VISIBLE);
            } else {
                mIsViewExpanded = false;
                valueAnimator = ValueAnimator.ofInt(mOriginalHeight + (int) (mOriginalHeight * 1), mOriginalHeight);
                mHiddenLayout.setVisibility(View.GONE);
            }
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                    view.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }

    // Provide a suitable constructor
    public ContactListAdapter(List<Contact> myDataset) {
        Collections.sort(myDataset, new ContactComparator());
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ContactListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String phoneNumber = mDataset.get(position).getPhoneNumber();
        phoneNumber = PhoneNumberUtils.formatNumber(phoneNumber);
        holder.mContactPhone.setText(phoneNumber);
        holder.mContactName.setText(mDataset.get(position).getFullName());
        holder.mContactEmail.setText(mDataset.get(position).getEmail());
        holder.mItemPosition.setText(""+position);
    }

    //delete item from list, and update list
    public void deleteItem(int itemPosition) {
        mDataset.remove(itemPosition);
        notifyItemRemoved(itemPosition);
    }

    //edit item from list, and update list
    public void editItem(Contact c, int itemPosition){
        mDataset.set(itemPosition, c);
        notifyItemChanged(itemPosition);
    }

    //add item to list, and update list
    public void addItem(Contact c){
        int position = 0;
        boolean isAdded = false;

        //go through dataset and add it in the correct position
        while(position < mDataset.size()){
            if (mDataset.get(position).getFullName().compareTo(c.getFullName()) > 0){
                mDataset.add(position, c);
                isAdded = true;
                break;
            }
            position++;
        }

        //if the item was not added, add it at the front or back
        if (!isAdded)
        {
            mDataset.add(position, c);
        }

        //update the view
        notifyItemInserted(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}