package it.unibo.citizenDigitalTwin.ui.home;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.ui.group_category_info.GroupCategoryInfoFragment;
import it.unibo.citizenDigitalTwin.ui.util.StateView;

public class HomeFragment extends Fragment implements GroupCategoryAdapter.GroupCategoryListener, StateView {

    private static final String STATE = "state";

    public static HomeFragment getInstance(final State state){
        final HomeFragment fragment = new HomeFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(state)){
            bundle.putSerializable(STATE, new HashMap<>(state.getState()));
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private State state;
    private TextView userName;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = new State();
        if(Objects.nonNull(getArguments())){
            final Map<LeafCategory, Data> data = (Map<LeafCategory, Data>)getArguments().getSerializable(STATE);
            if(Objects.nonNull(data)) {
                state = state.addMultipleData(data);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ActionBar actionBar = getActivity().getActionBar();
        if(Objects.nonNull(actionBar)){
            actionBar.setTitle("");
        }

        userName = root.findViewById(R.id.userName);

        final RecyclerView listView = root.findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(this.getContext(), 2);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        listView.setAdapter(new GroupCategoryAdapter(getContext(), this));

        final Optional<Data> userNameInfo = state.getData(LeafCategory.NAME);
        userNameInfo.ifPresent(name -> userName.setText(name.getValue()));

        return root;
    }

    @Override
    public void onGroupCategorySelected(final View view, final GroupCategory category) {
        if(Objects.nonNull(state) && state.getDataFromGroupCategory(category).size() > 0){
            final FragmentManager fragmentManager = getFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final Fragment fragment = GroupCategoryInfoFragment.getInstance(category, state);
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getContext(), R.string.no_data_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void newData(final State state){
        this.state = state;
        final Optional<Data> userNameInfo = state.getData(LeafCategory.NAME);
        userNameInfo.ifPresent(name -> userName.setText(name.getValue()));
    }
}
