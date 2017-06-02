package hospital;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Doctor implements Comparable<Doctor> {

	private String name;
	private List<Patient> patients;
	private int id;

	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@XmlElement
	public List<Patient> getPatients() {
		return patients;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}
	
	@Override
	public String toString() {
		String doctorString ="";
		for(Patient p:patients)
			doctorString+=p.toString();
		return name+" -- "+doctorString+"\n";
	}
	
	@Override
	public int compareTo(Doctor other) {
		return this.id - other.id;
	}

}
