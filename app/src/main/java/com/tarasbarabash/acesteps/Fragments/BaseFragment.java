package com.tarasbarabash.acesteps.Fragments;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tarasbarabash.acesteps.viewModels.BaseVM;
import com.tarasbarabash.acesteps.viewModels.FragmentVM;

/**
 * Created by Taras
 * 15-Sep-19, 21:08.
 */
public abstract class BaseFragment<B extends ViewDataBinding> extends Fragment {
    private B mBinding;
    protected abstract int getLayoutId();
    protected abstract FragmentVM<B> setViewModel(B binding);
    protected abstract int getMenuLayout();
    protected abstract boolean hasFragmentOptionsMenu();
    private FragmentVM<B> viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        viewModel = setViewModel(mBinding);
        return mBinding.getRoot();
    }

    public BaseVM<B> getViewModel() {
        return viewModel;
    }

    public B getBinding() {
        return mBinding;
    }
}
