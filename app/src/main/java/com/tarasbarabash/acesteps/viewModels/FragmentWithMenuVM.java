package com.tarasbarabash.acesteps.viewModels;

import androidx.databinding.ViewDataBinding;

import com.tarasbarabash.acesteps.Fragments.BaseFragment;

/**
 * Created by Taras
 * 21-Sep-19, 18:32.
 */
public abstract class FragmentWithMenuVM<B extends ViewDataBinding> extends FragmentVM<B> {
    public FragmentWithMenuVM(B binding, BaseFragment fragment) {
        super(binding, fragment);
    }
    public abstract boolean hasFragmentOptionsMenu();
    public abstract int getMenuLayout();
    public abstract boolean onMenuOptionSelected(int itemId);
}
