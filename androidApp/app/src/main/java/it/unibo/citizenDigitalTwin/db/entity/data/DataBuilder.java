package it.unibo.citizenDigitalTwin.db.entity.data;

import java.util.Date;
import java.util.Objects;

import it.unibo.citizenDigitalTwin.util.Builder;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;

public class DataBuilder implements Builder<Data> {

    private String identifier;
    private Date date;
    private Feeder feeder;
    private LeafCategory dataCategory;
    private String value;

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

    public DataBuilder dataCategory(final LeafCategory dataCategory) {
        this.dataCategory = dataCategory;
        return this;
    }

    public DataBuilder value(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public Data build() throws NullPointerException {
        Objects.requireNonNull(dataCategory);
        Objects.requireNonNull(value);
        Objects.requireNonNull(feeder);
        if (Objects.isNull(date)) {
            date = new Date();
        }
        return new Data(identifier,date,feeder,dataCategory,value);
    }

}
