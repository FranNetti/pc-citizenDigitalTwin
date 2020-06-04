package it.unibo.citizenDigitalTwin.ui.group_category_info;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;

class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.InformationHolder> {

    private static final List<CommunicationStandard> ORDER = Arrays.asList(CommunicationStandard.values());
    private static final Comparator<Pair<CommunicationStandard, String>> COMPARATOR =
            (a,b) -> Integer.compare(ORDER.indexOf(a.first), ORDER.indexOf(b.first));

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

    private final List<Pair<CommunicationStandard, String>> info;
    private final Context context;

    InformationAdapter(final Context context, final Map<CommunicationStandard, String> info){
        this.info = info.entrySet().stream()
                .map(x -> new Pair<>(x.getKey(), x.getValue()))
                .sorted(COMPARATOR)
                .collect(Collectors.toList());
        this.context = context;
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
            ((SingleInformationHolder)holder).valueText.setText(info.get(0).second);
        } else {
            final MultipleInformationHolder multiHolder = (MultipleInformationHolder) holder;
            final Pair<CommunicationStandard, String> info = this.info.get(position);
            multiHolder.informationTypeText.setText(info.first.getDisplayName(context));
            multiHolder.informationValueText.setText(info.second);
        }
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

}
