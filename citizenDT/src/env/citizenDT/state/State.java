// CArtAgO artifact code for project citizenDT

package citizenDT.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cartago.*;

public class State extends Artifact {
	
	private static final String PROP_NAME = "name";
	private static final String PROP_SURNAME = "surname";
	private static final String PROP_PIPPO = "pippo";
	private final Map<State.LeafCategory, String> state = new HashMap<>();
	
	void init(final String name, final String surname) {
		defineObsProperty(PROP_NAME, name);
		defineObsProperty(PROP_SURNAME, surname);
		defineObsProperty(PROP_PIPPO, new Pippo());
	}

	@OPERATION
	void updateState(final String name, final String surname) {
		final ObsProperty propName = getObsProperty(PROP_NAME);
		propName.updateValue(name);
		
		final ObsProperty propPippo = getObsProperty(PROP_PIPPO);
		Object val = propPippo.getValue();
		System.out.println("obj non castato");
		System.out.println(val);
		if (val instanceof Pippo) {
			Pippo pippo = (Pippo) val;
			System.out.println("proprietà di pippo");
			System.out.println(pippo.getName());
		}
		
		signal("tick");
	}
	
	private class Pippo {
		private String name = "pippo";
		public String getName() {
			return name;
		}
	}
	
	private enum LeafCategory {
		NAME("name");
		
		private final String name;
		
		LeafCategory(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
}

