package citizenDT.state;

public enum LeafCategory {
	
	NAME("name", "Nome"),
	SURNAME("surname", "Cognome"),
	BIRTHDATE("birthdate", "Data di nascita"),
	ADDRESS("address", "Indirizzo");
	
	private final String name;
	private final String displayName;
	
	LeafCategory(final String name, final String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}
