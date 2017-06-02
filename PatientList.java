package hospital;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Patients")
public class PatientList {
	private AtomicInteger patientId;
	private List<Patient> plist;
	
	public PatientList(){
		plist = new CopyOnWriteArrayList<Patient>();
		patientId = new AtomicInteger();
	}

	public AtomicInteger getPatientId() {
		return patientId;
	}

	public void setPatientId(AtomicInteger patientId) {
		this.patientId = patientId;
	}

	@XmlElement(name="Patient")
	public List<Patient> getPlist() {
		return plist;
	}

	public void setPlist(List<Patient> plist) {
		this.plist = plist;
	}
	
	public int addPatients(String patient_name, String insurance_num) {
		int id = patientId.incrementAndGet();
		Patient p = new Patient();
		p.setInsurance_num(insurance_num);
		p.setPatient_id(id);
		p.setPatient_name(patient_name);
		plist.add(p);
		return id;
	}

	@Override
	public String toString(){
		String pat="";
		for(Patient p:plist)
			pat+=p.toString();
		return  pat;
	}

	public Patient find(int id) {
		Patient pat = null;
		for(Patient d : plist){
			if(d.getPatient_id()==id){
				pat=d;
				break;
			}
		}
		return pat;
	}
}
