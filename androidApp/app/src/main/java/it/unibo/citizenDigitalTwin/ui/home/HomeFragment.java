package it.unibo.citizenDigitalTwin.ui.home;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Optional;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.artifact.MainUI;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.ui.group_category_info.GroupCategoryInfoFragment;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;
import it.unibo.citizenDigitalTwin.ui.util.StateViewer;

public class HomeFragment extends FragmentWithId implements GroupCategoryAdapter.GroupCategoryListener, StateViewer {

    private static final String FRAGMENT_ID = "HOME";
    private static final String ARTIFACT = "artifact";

    public static HomeFragment getInstance(final MainUI.MainUIMediator artifact){
        final HomeFragment fragment = new HomeFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(artifact)){
            bundle.putParcelable(ARTIFACT, artifact);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private State state;
    private MainUI.MainUIMediator artifact;
    private TextView userName;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = new State();
        if(Objects.nonNull(getArguments())){
            artifact = getArguments().getParcelable(ARTIFACT);
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

        /*
            Used for getting newest state information in case the back button is pressed from
            GroupCategoryInfoFragment since it's not detected from the MainUI.
         */
        if(Objects.nonNull(artifact)) {
            artifact.newSubView(this);
        }
        return root;
    }

    @Override
    public String getFragmentId() {
        return FRAGMENT_ID;
    }

    @Override
    public void onGroupCategorySelected(final View view, final GroupCategory category) {
        if(Objects.nonNull(state) && state.getDataFromGroupCategory(category).size() > 0){
            final FragmentManager fragmentManager = getFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            final GroupCategoryInfoFragment fragment = GroupCategoryInfoFragment.getInstance(category, state);
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            if(Objects.nonNull(artifact)){
                artifact.newSubView(fragment);
            }
        } else {
            Toast.makeText(getContext(), R.string.no_data_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void newData(final State state){
        this.state = state;
        final Optional<Data> userNameInfo = state.getData(LeafCategory.NAME);
        userNameInfo.ifPresent(name -> {
            if(Objects.nonNull(userName)){
                userName.setText(name.getInformation().get(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER));
            }
        });
    }
}
