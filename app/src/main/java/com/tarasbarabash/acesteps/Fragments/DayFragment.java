package com.tarasbarabash.acesteps.Fragments;

import com.tarasbarabash.acesteps.R;
import com.tarasbarabash.acesteps.viewModels.DayVM;
import com.tarasbarabash.acesteps.databinding.FragmentDayBinding;
import com.tarasbarabash.acesteps.viewModels.FragmentVM;


public class DayFragment extends BaseFragment<FragmentDayBinding> {

    @Override
    public void onResume() {
        super.onResume();
        getBinding().invalidateAll();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_day;
    }

    @Override
    protected FragmentVM<FragmentDayBinding> setViewModel(FragmentDayBinding binding) {
        DayVM viewModel = new DayVM(binding, this);
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
