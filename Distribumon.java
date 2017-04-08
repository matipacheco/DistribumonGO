import java.io.Serializable;

class Distribumon implements Serializable {
	int id, nivel;
	String nombre;

	public Distribumon(int id, int nivel, String nombre) {
		this.id 	= id;
		this.nivel 	= nivel;
		this.nombre = nombre;
	}
}