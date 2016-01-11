package ia;

import database.Recurso;

public interface Solver {
	public void init();
	public void scheduleSolution(Recurso[] recursos);
}
