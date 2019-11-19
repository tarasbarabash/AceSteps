package com.tarasbarabash.acesteps.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tarasbarabash.acesteps.Activities.WorkoutActivity;
import com.tarasbarabash.acesteps.Constants.BundleKeys;
import com.tarasbarabash.acesteps.models.WorkoutSession;
import com.tarasbarabash.acesteps.databinding.WorkoutItemBinding;

import java.util.ArrayList;

/**
 * Created by Taras
 * 14-Sep-19, 16:47.
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {
    private static final String TAG = WorkoutsAdapter.class.getSimpleName();
    private ArrayList<WorkoutSession> mList;
    private Context mContext;

    public WorkoutsAdapter(Context context, ArrayList<WorkoutSession> list, RecyclerView recyclerView) {
        mList = list;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        WorkoutItemBinding itemBinding = WorkoutItemBinding.inflate(layoutInflater, viewGroup,
                                                                    false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        WorkoutSession session = mList.get(i);
        viewHolder.bind(session);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private WorkoutItemBinding mBinding;

        ViewHolder(WorkoutItemBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        void bind(WorkoutSession item) {
            mBinding.setSession(item);
            mBinding.getRoot().setOnClickListener((v) -> {
                Intent intent = new Intent(mContext, WorkoutActivity.class);
                intent.putExtra(BundleKeys.WORKOUT_ID_NOTIFICATION, item.getId().toString());
                mContext.startActivity(intent);
            });
            mBinding.executePendingBindings();
        }
    }

    public void addItem(WorkoutSession newItem) {
        mList.add(0, newItem);
        notifyItemInserted(0);
    }

    public void removeItem(String id) {
        int index = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getId().toString().equals(id)) {
                index = i;
                break;
            }
        }
        if (index < 0) return;
        mList.remove(index);
        notifyItemRemoved(index);
    }
}
