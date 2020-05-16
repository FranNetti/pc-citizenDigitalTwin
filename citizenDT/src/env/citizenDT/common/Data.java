package citizenDT.common;

import java.util.Date;
import java.util.Optional;

public class Data {
	
	private final Optional<String> uri;
	private final Date date;
	private final String feeder;
	private final LeafCategory dataCategory;
	private final String value;
	
	Data(final String uri, final Date date, final String feeder, final LeafCategory dataCategory, final String value) {
		super();
		this.uri = Optional.ofNullable(uri);
		this.date = date;
		this.feeder = feeder;
		this.dataCategory = dataCategory;
		this.value = value;
	}

	public Optional<String> getUri() {
		return uri;
	}

	public Date getDate() {
		return date;
	}

	public String getFeeder() {
		return feeder;
	}

	public LeafCategory getDataCategory() {
		return dataCategory;
	}

	public String getValue() {
		return value;
	}
	

}
