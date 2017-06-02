package hospital;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/resourcesDr")
public class JAX_Application extends Application{
	@Override
	public  Set<Class<?>> getClasses(){
		Set<Class<?>> set= new HashSet<Class<?>>();
		set.add(DoctorResource.class);
		set.add(PatientResource.class);
		return set;
	}
}
