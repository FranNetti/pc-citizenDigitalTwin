package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;

class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationHolder> {

    abstract static class InformationHolder extends RecyclerView.ViewHolder {
        public InformationHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class MultipleInformationHolder extends InformationHolder {

        final TextView informationTypeText;
        final TextView informationValueText;

        public MultipleInformationHolder(@NonNull View itemView) {
            super(itemView);
            informationTypeText = itemView.findViewById(R.id.informationTypeText);
            informationValueText = itemView.findViewById(R.id.informationValueText);
        }
    }

    private static class SingleInformationHolder extends InformationHolder {

        final TextView valueText;

        public SingleInformationHolder(@NonNull View itemView) {
            super(itemView);
            this.valueText = itemView.findViewById(R.id.singleItemValue);
        }
    }

    private final Map<CommunicationStandard, String> info;
    private final List<CommunicationStandard> keys;
    private final Context context;

    InformationAdapter(final Context context, final Map<CommunicationStandard, String> info){
        this.info = info;
        this.context = context;
        this.keys = new ArrayList<>(info.keySet());
    }


    @NonNull
    @Override
    public InformationHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final ConstraintLayout singleLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_information_item, parent, false);
        final ConstraintLayout multipleLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_information_item, parent, false);
        return getItemCount() > 1 ? new MultipleInformationHolder(multipleLayout) : new SingleInformationHolder(singleLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull final InformationHolder holder, final int position) {
        if(getItemCount() == 1){
            info.forEach((k,v) -> ((SingleInformationHolder)holder).valueText.setText(v));
        } else {
            final MultipleInformationHolder multiHolder = (MultipleInformationHolder) holder;
            final CommunicationStandard infoType = keys.get(position);
            multiHolder.informationTypeText.setText(infoType.getDisplayName(context));
            multiHolder.informationValueText.setText(info.get(infoType));
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

}
