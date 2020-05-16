// CArtAgO artifact code for project citizenDT

package citizenDT.connection;

import java.util.Arrays;
import java.util.List;

import cartago.*;
import citizenDT.common.Data;
import citizenDT.common.DataBuilder;
import citizenDT.common.LeafCategory;

public class ConnectionManager extends Artifact {
	
	private static final long WAIT_TIME = 5000;
	private List<List<Data>> fakeStates = Arrays.asList(
			Arrays.asList(
					new DataBuilder()
						.dataCategory(LeafCategory.NAME)
						.value("Stefano")
						.feeder("Stefano")
						.build(),
					new DataBuilder()
						.dataCategory(LeafCategory.SURNAME)
						.value("Righini")
						.feeder("Stefano")
						.build()
					),
			Arrays.asList(
					new DataBuilder()
						.dataCategory(LeafCategory.BIRTHDATE)
						.value("24-10-1996")
						.feeder("Stefano")
						.build()
					),
			Arrays.asList(
					new DataBuilder()
						.dataCategory(LeafCategory.ADDRESS)
						.value("Via Luca Ghini 4")
						.feeder("Stefano")
						.build()
					)
			);
	
	void init() {
		execInternalOp("generateData");
	}

	@OPERATION
	void send(final List<Data> state) {
		state.forEach(data -> {
			System.out.println("{data_category: " + data.getDataCategory() + " | value: " + data.getValue() + "}");
		});
	}
	
	@OPERATION
	void authenticate(final String username, final String password, final OpFeedbackParam<Boolean> res) {
		//contatta server autenticazione e attende risposta
		res.set(true); // si è autenticato correttamente
	}
	
	@INTERNAL_OPERATION
	void generateData() {
		final int length = fakeStates.size();
		for (int i = 0; i < length; i++) {
			await_time(WAIT_TIME);
			final List<Data> state = fakeStates.get(i);
			signal("newState",state);
		}
	}
}

