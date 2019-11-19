package com.tarasbarabash.acesteps.Fragments;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tarasbarabash.acesteps.viewModels.BaseVM;
import com.tarasbarabash.acesteps.viewModels.FragmentWithMenuVM;

/**
 * Created by Taras
 * 15-Sep-19, 21:08.
 */
public abstract class BaseFragmentWithMenu<B extends ViewDataBinding> extends BaseFragment<B> {
    protected abstract int getLayoutId();
    protected abstract FragmentWithMenuVM<B> setViewModel(B binding);
    protected abstract int getMenuLayout();
    protected abstract boolean hasFragmentOptionsMenu();
    private FragmentWithMenuVM<B> viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        B binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        viewModel = setViewModel(binding);
        setHasOptionsMenu(viewModel.hasFragmentOptionsMenu());
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId = viewModel.getMenuLayout();
        if (menuId == -1) return;
        inflater.inflate(menuId, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return viewModel.onMenuOptionSelected(item.getItemId());
    }

    public BaseVM<B> getViewModel() {
        return viewModel;
    }
}
