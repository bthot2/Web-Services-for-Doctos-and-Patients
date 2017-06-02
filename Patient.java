
package hospital;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Patient {

	private String patient_name;
	private String insurance_num;
	private int patient_id;
	
	@XmlElement
	public String getPatient_name() {
		return patient_name;
	}
	public void setPatient_name(String patient_name) {
		this.patient_name = patient_name;
	}
	
	@XmlElement
	public String getInsurance_num() {
		return insurance_num;
	}
	public void setInsurance_num(String insurance_num) {
		this.insurance_num = insurance_num;
	}

	@XmlElement
	public int getPatient_id() {
		return patient_id;
	}
	public void setPatient_id(int patient_id) {
		this.patient_id = patient_id;
	}
	
	@Override
	public String toString(){
		return String.format("%2d: ", patient_id) + patient_name +
		" ==> " + insurance_num+"\n";
	}
}
