package hospital;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Doctors")
public class DoctorList {

	private List<Doctor> doctor;
	private AtomicInteger doctorId;
	

	public DoctorList() {
		doctor = new CopyOnWriteArrayList<Doctor>();
		doctorId = new AtomicInteger();
	}

	@XmlElement(name="Doctor")
	public List<Doctor> getDoctorList() {
		return doctor;
	}

	public void setDoctorList(List<Doctor> doctorList) {
		this.doctor=doctorList;
	}

	@Override
	public String toString() {
		String doctorString = "";
		for (Doctor d : doctor)
			doctorString+=d.toString();
		return doctorString;
	}

	public int addDoctors(String name, List<Patient> plist) {
		int id = doctorId.incrementAndGet();
		Doctor d = new Doctor();
		d.setId(id);
		d.setName(name);
		d.setPatients(plist);
		doctor.add(d);
		return id;
	}

	public Doctor find(int id) {
		Doctor doc = null;
		for(Doctor d : doctor){
			if(d.getId()==id){
				doc=d;
				break;
			}
		}
		return doc;
	}
}
