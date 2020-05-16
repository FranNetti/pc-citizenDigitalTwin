package citizenDT.common;

public class Feeder {
	
	private final boolean isResource;
	private final String uri;
	private final String name;
	
	public Feeder(final boolean isResource, final String uri, final String name) {
		this.isResource = isResource;
		this.uri = uri;
		this.name = name;
	}
	
	public Feeder(final String uri, final String name) {
		this(true,uri,name);
	}

	public boolean isResource() {
		return isResource;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}
	
}
