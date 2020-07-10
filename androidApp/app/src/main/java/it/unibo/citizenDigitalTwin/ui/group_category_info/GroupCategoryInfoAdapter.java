package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;

class GroupCategoryInfoAdapter extends RecyclerView.Adapter<GroupCategoryInfoAdapter.GroupCategoryInfoHolder> {

    static class GroupCategoryInfoHolder extends RecyclerView.ViewHolder {

        final ConstraintLayout layout;
        final ConstraintLayout contentLayout;
        final TextView leafCategoryName;
        final TextView feederName;
        final TextView dateText;
        final ImageView expandImage;
        final RecyclerView recyclerView;

        boolean expanded;

        public GroupCategoryInfoHolder(final ConstraintLayout view) {
            super(view);
            expanded = false;
            this.layout = view;
            this.contentLayout = view.findViewById(R.id.contentLayout);
            this.leafCategoryName = view.findViewById(R.id.leafCategoryName);
            this.expandImage = view.findViewById(R.id.expandElement);
            this.feederName = view.findViewById(R.id.feederName);
            this.dateText = view.findViewById(R.id.dateField);
            this.recyclerView = view.findViewById(R.id.informationRecyclerView);
        }
    }

    private final List<Data> dataList;
    private final Context context;

    GroupCategoryInfoAdapter(final Context context, final List<Data> dataList){
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupCategoryInfoHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaf_data_item, parent, false);
        return new GroupCategoryInfoHolder(layout);
    }

    @Override
    public void onBindViewHolder(final GroupCategoryInfoHolder holder, final int position) {
        final Data data = dataList.get(position);
        holder.contentLayout.setVisibility(holder.expanded ? View.VISIBLE : View.GONE);
        final View.OnClickListener listener = v -> {
            holder.expanded = !holder.expanded;
            GroupCategoryInfoAdapter.this.notifyDataSetChanged();
        };
        holder.layout.setOnClickListener(listener);
        final int arrowImage;
        if(holder.expanded){
            arrowImage = R.drawable.ic_baseline_keyboard_arrow_up_24;
        } else {
            arrowImage = R.drawable.ic_baseline_keyboard_arrow_down_24;
        }
        holder.expandImage.setImageDrawable(context.getDrawable(arrowImage));

        holder.leafCategoryName.setText(data.getLeafCategory().getDisplayName(context));
        final String feeder = data.getFeeder().getName().isEmpty() ?
                data.getFeeder().getUri() : data.getFeeder().getName();
        holder.feederName.setText(feeder);

        final DateFormat d = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        holder.dateText.setText(d.format(data.getDate()));

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setHasFixedSize(true);


        final Map<CommunicationStandard, String> dataInfo = data.getInformation();
        if(!dataInfo.containsKey(CommunicationStandard.DEFAULT_UNIT_OF_MEASURE_IDENTIFIER)){
            data.getLeafCategory().getUm().ifPresent(um ->
                    dataInfo.put(CommunicationStandard.DEFAULT_UNIT_OF_MEASURE_IDENTIFIER, um)
            );
        }

        final InformationAdapter adapter = new InformationAdapter(context, listener, dataInfo);
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
