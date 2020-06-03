package it.unibo.citizenDigitalTwin.db.entity.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.unibo.citizenDigitalTwin.data.Builder;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;

public class DataBuilder implements Builder<Data> {

    private String identifier;
    private Date date;
    private Feeder feeder;
    private LeafCategory dataCategory;
    private Map<String, String> information = new HashMap<>();

    public DataBuilder uri(final String uri) {
        this.identifier = uri;
        return this;
    }

    public DataBuilder date(final Date date) {
        this.date = date;
        return this;
    }

    public DataBuilder feeder(final Feeder feeder) {
        this.feeder = feeder;
        return this;
    }

    public DataBuilder leafCategory(final LeafCategory dataCategory) {
        this.dataCategory = dataCategory;
        return this;
    }

    public DataBuilder addInformation(final String informationType, final String value) {
        Objects.requireNonNull(informationType);
        Objects.requireNonNull(value);
        if(informationType.isEmpty() || informationType.trim().isEmpty() || value.isEmpty() || value.trim().isEmpty()){
            throw new IllegalArgumentException("The parameters must not be empty!");
        }
        information.put(informationType, value);
        return this;
    }

    @Override
    public Data build() throws NullPointerException, IllegalStateException {
        Objects.requireNonNull(dataCategory);
        Objects.requireNonNull(feeder);
        if(information.isEmpty()){
            throw new IllegalStateException("There is no information!");
        }
        if (Objects.isNull(date)) {
            date = new Date();
        }
        return new Data(identifier,date,feeder,dataCategory,information);
    }

}
