package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.MainActivity;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.view_model.MainActivityViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupCategoryInfoFragment extends Fragment {

    private static final String GROUP_CATEGORY = "groupCategory";

    private GroupCategory groupCategory;

    public static Bundle getBundle(final GroupCategory groupCategory) {
        Bundle args = new Bundle();
        args.putSerializable(GROUP_CATEGORY, groupCategory);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupCategory = (GroupCategory) getArguments().getSerializable(GROUP_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_group_category_info, container, false);
        final ActionBar actionBar = ((MainActivity)requireActivity()).getSupportActionBar();
        if(Objects.nonNull(actionBar)){
            actionBar.setTitle(groupCategory.getDisplayName(requireContext()));
        }

        final List<Data> data = new ArrayList<>();

        final RecyclerView listView = root.findViewById(R.id.groupRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);

        final GroupCategoryInfoAdapter adapter = new GroupCategoryInfoAdapter(getContext(), data);
        listView.setAdapter(adapter);

        new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).home
                .getState().observe(getViewLifecycleOwner(), state -> {
            final List<Data> dataUpdated = state.getDataFromGroupCategory(groupCategory);
            data.clear();
            data.addAll(dataUpdated);
            adapter.notifyDataSetChanged();
        });

        return root;
    }
}
