package citizenDT.common;

import java.util.Date;
import java.util.Objects;

public class DataBuilder implements Builder<Data> {
	
	private String uri;
	private Date date;
	private String feeder;
	private LeafCategory dataCategory;
	private String value;

	public DataBuilder uri(final String uri) {
		this.uri = uri;
		return this;
	}

	public DataBuilder date(final Date date) {
		this.date = date;
		return this;
	}

	public DataBuilder feeder(final String feeder) {
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
		if (Objects.isNull(date))
			date = new Date();
		return new Data(uri,date,feeder,dataCategory,value);
	}
	
}
