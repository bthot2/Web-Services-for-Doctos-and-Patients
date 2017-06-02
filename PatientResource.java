package hospital;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/patient")
public class PatientResource {

	@Context
	private ServletContext svtx;
	private static DoctorList dlist;
	private static PatientList plist;
	private static int i = 0;

	public PatientResource() {

	}

	@GET
	@Path("/xml")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getXml() {
		checkContext();
		return Response.ok(plist, "application/xml").build();
	}

	@GET
	@Path("/xml/{id: \\d+}")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getOneXml(@PathParam("id") int id) {
		checkContext();
		return toRequestedType(id, "application/xml");
	}

	@GET
	@Path("/plain")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getPlain() {
		checkContext();
		return plist.toString();
	}

	@GET
	@Path("/plain/{id: \\d+}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getOnePlain(@PathParam("id") int id) {
		checkContext();
		Patient p = plist.find(id);
		if (p == null)
			return "Ptient with id" + id + "not found";
		else
			return p.toString();
	}

	@POST
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/create")
	public Response create(@FormParam("p_name") String p_name, @FormParam("ins") String ins) {
		checkContext();
		String msg = null;
		// Require both properties to create.
		if (p_name == null || ins == null) {
			msg = "Property 'patient name' or 'insurance number' is missing.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}

		int id = plist.addPatients(p_name, ins);
		msg = "Patient " + id + " created: (name = " + p_name + " and insurance number: " + ins + "is created).\n";
		return Response.ok(msg, "text/plain").build();
	}

	@DELETE
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/delete/{id: \\d+}")
	public Response delete(@PathParam("id") int id) {
		checkContext();
		String msg = null;
		Patient p = plist.find(id);
		if (p == null) {
			msg = "There is no patient with ID " + id + ". Cannot delete.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
		List<Doctor> doctors = dlist.getDoctorList();
		for (Doctor d : doctors) {
			List<Patient> pat = d.getPatients();
			for (int j = 0; j < pat.size(); j++) {
				if (pat.get(j).equals(p))
					pat.remove(j);
			}
		}
		plist.getPlist().remove(p);
		msg = "Patient " + id + " deleted.\n";

		return Response.ok(msg, "text/plain").build();
	}

	@PUT
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/update")
	public Response update(@FormParam("id") int id, @FormParam("p_name") String p_name, @FormParam("ins") String ins) {
		checkContext();

		// Check that sufficient data are present to do an edit.
		String msg = null;
		if (p_name == null && ins == null)
			msg = "Neither patient name nor his insurance number were given: nothing to edit.\n";

		Patient p = plist.find(id);
		if (p == null)
			msg = "There is no doctor with ID " + id + "\n";

		if (msg != null)
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		// Update.
		if (p_name != null)
			p.setPatient_name(p_name);
		if (ins != null)
			p.setInsurance_num(ins);

		msg = "Patient " + id + " has been updated.\n";
		return Response.ok(msg, "text/plain").build();
	}

	private Response toRequestedType(int id, String type) {
		Patient pat = plist.find(id);
		if (pat == null) {
			String msg = "Patient with Id:" + id + " doesn't exists.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		} else
			return Response.ok(pat, type).build();
	}
	
	public void checkContext() {
		if(plist==null)
			populatePatients();
		if (dlist == null) 
			populateDoctors();
	}

	public void populateDoctors() {
		dlist=new DoctorList();
		String filename = "/WEB-INF/data/doctors.db";
		InputStream in = svtx.getResourceAsStream(filename);

		// Read the data into the array of Predictions.
		if (in != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String record = null;
				while ((record = reader.readLine()) != null) {
					String[] parts = record.split("!");
					addDoctors(parts[0], parts[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void addDoctors(String name, String cnt) {
		int count = Integer.parseInt(cnt);
		List<Patient> patientList = new ArrayList<>();
		int loop = i + count;
		for (int j = i; j < loop; j++) {
			Patient p = plist.getPlist().get(j);
			patientList.add(p);
			i++;
		}
		dlist.addDoctors(name, patientList);
	}

	public void populatePatients() {
		plist = new PatientList();
		String filename = "/WEB-INF/data/patients.db";
		InputStream in = svtx.getResourceAsStream(filename);

		if (in != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String record = null;
				while ((record = reader.readLine()) != null) {
					String[] parts = record.split("!");
					addPatients(parts[0], parts[1]);
				}
			} catch (Exception e) {
				throw new RuntimeException("I/O failed!");
			}
		}
	}

	public void addPatients(String patient_name, String insurance_num) {
		plist.addPatients(patient_name, insurance_num);
	}

}
