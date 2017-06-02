package hospital;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("/doctor")
public class DoctorResource {

	@Context
	private ServletContext svtx;
	private static DoctorList dlist;
	private static PatientList plist;
	private static int i = 0;

	public DoctorResource() {

	}

	@GET
	@Path("/xml")
	@Produces({ MediaType.APPLICATION_XML })
	public Response getXml() {
		checkContext();
		return Response.ok(dlist, "application/xml").build();
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
		return dlist.toString();
	}

	@GET
	@Path("/plain/{id: \\d+}")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getOnePlain(@PathParam("id") int id) {
		checkContext();
		Doctor d = dlist.find(id);
		if (d == null)
			return "Doctor with id" + id + "not found";
		else
			return d.toString();
	}

	@POST
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/create")
	public Response create(@FormParam("doc_name") String doc_name, @FormParam("patients") String patients) {
		checkContext();
		String msg = null;
		// Require both properties to create.
		if (doc_name == null || patients == null) {
			msg = "Property 'who' or 'what' is missing.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
		List<Patient> patientList = new CopyOnWriteArrayList<Patient>();
		String[] list = patients.split("#");
		for (int j = 0; j < list.length; j++) {
			addPatients(list[j].split("!")[0], list[j].split("!")[1]);
		}
		int size = plist.getPlist().size();
		for(int j=size-list.length;j<size;j++){
			Patient p= plist.getPlist().get(j);
			patientList.add(p);
		}
		int id = dlist.addDoctors(doc_name, patientList);
		msg = "Doctor " + id + " created: (name = " + doc_name + " with" + list.length + "patients).\n";
		return Response.ok(msg, "text/plain").build();
	}

	@DELETE
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/delete/{id: \\d+}")
	public Response delete(@PathParam("id") int id) {
		checkContext();
		String msg = null;
		Doctor p = dlist.find(id);
		if (p == null) {
			msg = "There is no doctor with ID " + id + ". Cannot delete.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		}
		dlist.getDoctorList().remove(p);
		msg = "Doctor " + id + " deleted.\n";

		return Response.ok(msg, "text/plain").build();
	}

	@PUT
	@Produces({ MediaType.TEXT_PLAIN })
	@Path("/update")
	public Response update(@FormParam("id") int id, @FormParam("doc_name") String doc_name,
			@FormParam("patients") String patients) {
		checkContext();

		// Check that sufficient data are present to do an edit.
		String msg = null;
		if (doc_name == null && patients == null)
			msg = "Neither doctor name nor list of patients is given: nothing to edit.\n";

		Doctor d = dlist.find(id);
		if (d == null)
			msg = "There is no doctor with ID " + id + "\n";

		if (msg != null)
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		// Update.
		if (doc_name != null)
			d.setName(doc_name);

		msg = "Doctor " + id + " has been updated.\n";
		return Response.ok(msg, "text/plain").build();
	}

	private Response toRequestedType(int id, String type) {
		Doctor doc = dlist.find(id);
		if (doc == null) {
			String msg = "Doctor with Id:" + id + " doesn't exists.\n";
			return Response.status(Response.Status.BAD_REQUEST).entity(msg).type(MediaType.TEXT_PLAIN).build();
		} else
			return Response.ok(doc, type).build();
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
