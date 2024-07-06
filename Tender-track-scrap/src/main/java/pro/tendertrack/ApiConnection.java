package pro.tendertrack;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiConnection {
    private static final String ALFRESCO_ENDPOINT = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom";
    private static final String ALFRESCO_USERNAME = "admin";
    private static final String ALFRESCO_PASSWORD = "admin";

    public Session createSession() {
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<>();
        parameter.put(SessionParameter.USER, ALFRESCO_USERNAME);
        parameter.put(SessionParameter.PASSWORD, ALFRESCO_PASSWORD);
        parameter.put(SessionParameter.ATOMPUB_URL, ALFRESCO_ENDPOINT);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        List<Repository> repositories = factory.getRepositories(parameter);
        return repositories.get(0).createSession();
    }
}
