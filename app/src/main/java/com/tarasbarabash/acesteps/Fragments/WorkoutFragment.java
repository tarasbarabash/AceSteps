package com.tarasbarabash.acesteps.Fragments;

import android.os.Bundle;

import com.tarasbarabash.acesteps.Constants.BundleKeys;
import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.databinding.FragmentWorkoutBinding;
import com.tarasbarabash.acesteps.viewModels.FragmentVM;
import com.tarasbarabash.acesteps.viewModels.FragmentWithMenuVM;
import com.tarasbarabash.acesteps.viewModels.WorkoutVM;

import java.util.Objects;

/**
 * Created by Taras
 * 20-Sep-19, 18:32.
 */
public class WorkoutFragment extends BaseFragmentWithMenu<FragmentWorkoutBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_workout;
    }

    @Override
    protected FragmentWithMenuVM<FragmentWorkoutBinding> setViewModel(FragmentWorkoutBinding binding) {
        String workoutId = null;
        Bundle bundle = Objects.requireNonNull(getActivity()).getIntent().getExtras();
        if (bundle != null) workoutId = bundle.getString(BundleKeys.WORKOUT_ID_NOTIFICATION);
        WorkoutVM viewModel = new WorkoutVM(binding, this, workoutId);
        binding.setViewModel(viewModel);
        return viewModel;
    }

    @Override
    protected int getMenuLayout() {
        return 0;
    }

    @Override
    protected boolean hasFragmentOptionsMenu() {
        return false;
    }
}
