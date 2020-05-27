package it.unibo.citizenDigitalTwin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.ui.group_category_info.GroupCategoryInfoFragment;
import it.unibo.citizenDigitalTwin.view_model.MainActivityViewModel;
import it.unibo.citizenDigitalTwin.view_model.HomeViewModel;

public class HomeFragment extends Fragment implements GroupCategoryAdapter.GroupCategoryListener {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).home;
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView userName = root.findViewById(R.id.userName);

        final RecyclerView listView = root.findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(this.getContext(), 2);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        listView.setAdapter(new GroupCategoryAdapter(getContext(), this));

        homeViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            final Optional<Data> userNameInfo = state.getData(LeafCategory.NAME);
            userNameInfo.ifPresent(name -> userName.setText(name.getValue()));
        });

        return root;
    }

    @Override
    public void onGroupCategorySelected(final View view, final GroupCategory category) {
        final State state = homeViewModel.getState().getValue();
        if(Objects.nonNull(state) && state.getDataFromGroupCategory(category).size() > 0){
            final Bundle bundle = GroupCategoryInfoFragment.getBundle(category);
            Navigation.createNavigateOnClickListener(R.id.expand_group_category, bundle)
                    .onClick(view);
        } else {
            Toast.makeText(getContext(), R.string.no_data_available, Toast.LENGTH_SHORT).show();
        }
    }
}
