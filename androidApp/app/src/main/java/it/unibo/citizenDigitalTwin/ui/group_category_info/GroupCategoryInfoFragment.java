package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;
import it.unibo.citizenDigitalTwin.ui.util.StateView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupCategoryInfoFragment extends FragmentWithId implements StateView {

    private static final String FRAGMENT_ID = "GROUP_CATEGORY";
    private static final String GROUP_CATEGORY = "groupCategory";
    private static final String STATE = "state";

    public static GroupCategoryInfoFragment getInstance(final GroupCategory groupCategory, final State state) {
        final GroupCategoryInfoFragment fragment = new GroupCategoryInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP_CATEGORY, groupCategory);
        args.putSerializable(STATE, new HashMap<>(state.getState()));
        fragment.setArguments(args);
        return fragment;
    }

    private GroupCategory groupCategory;
    private State state;
    private List<Data> data;
    private GroupCategoryInfoAdapter adapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = new State();
        if (Objects.nonNull(getArguments())) {
            groupCategory = (GroupCategory) getArguments().getSerializable(GROUP_CATEGORY);
            state = state.addMultipleData((Map<LeafCategory, Data>)getArguments().getSerializable(STATE));
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_group_category_info, container, false);
        final ActionBar actionBar = getActivity().getActionBar();
        if(Objects.nonNull(actionBar) && Objects.nonNull(groupCategory)){
            actionBar.setTitle(groupCategory.getDisplayName(getContext()));
        }

        final RecyclerView listView = root.findViewById(R.id.groupRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);

        data = state.getDataFromGroupCategory(groupCategory);

        adapter = new GroupCategoryInfoAdapter(getContext(), data);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public String getFragmentId() {
        return FRAGMENT_ID;
    }

    @Override
    public void newData(final State state){
        data.clear();
        data.addAll(state.getDataFromGroupCategory(groupCategory));
        adapter.notifyDataSetChanged();
    }

}
