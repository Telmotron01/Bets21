package domain;

import java.io.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Pronostico implements Serializable {
	@Id
	@GeneratedValue
	private Integer pronosticoNumber;
	private String descripcion;
	private float cuota;
	private Question pregunta;
	
	public Pronostico(String pronostico, float cuota, Question q) {
		this.descripcion=pronostico;
		this.cuota=cuota;
		this.pregunta=q;
	}

	public Integer getPronosticoNumber() {
		return pronosticoNumber;
	}

	public void setPronosticoNumber(Integer pronosticoNumber) {
		this.pronosticoNumber = pronosticoNumber;
	}

	public String getPronostico() {
		return descripcion;
	}

	public void setPronostico(String pronostico) {
		this.descripcion = pronostico;
	}

	public float getCuota() {
		return cuota;
	}

	public void setCuota(float cuota) {
		this.cuota = cuota;
	}

	@Override
	public String toString() {
		return "Pronostico=" + descripcion + ", cuota=" + cuota;
	}

	public Question getPregunta() {
		return pregunta;
	}

	public void setPregunta(Question pregunta) {
		this.pregunta = pregunta;
	}
	
	
	
	
}
