drpatients.war - A web archive file which is used for the deploying the service.
build.xml - A script used for the creating the war file, creating the necessary folders and  files. Change the tomcat lib directory to your directory where Apache tomcat is located.
src - A folder containing the all source files and necessary libraries.
Files inside src:
doctors.db - A sample doctors file which is used for populating doctors. Each string is seperated by !.
patients.db - A sample patients file which is used for populating patients. Each string is seperated by !.
web.xml - Used for invoking the servlet class.
Files inside hospital package:
Doctor.java - A bean class for doctor.
Patient.java - A bean class for patient.
DoctorList.java - An utility class for adding the doctors for list, finding the specific doctor.
PatientList.java - An utility class for adding patients for list, finding the specific patient.
JAX_Application.java - Class that iniaties the application.
DoctorResource.java - Actual implementation of the operations for Doctor resource.
PatientResource.java - Actual implementation of the operations for Patient resource.
