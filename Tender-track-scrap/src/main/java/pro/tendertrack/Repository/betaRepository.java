package pro.tendertrack.Repository;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class betaRepository {

    private static final String ALFRESCO_ENDPOINT = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom";
    private static final String ALFRESCO_USERNAME = "admin";
    private static final String ALFRESCO_PASSWORD = "admin";


//    public void uploadFiles() {
//        try {
//            BetaService betaService = new BetaService();
//            // Create a CMIS session
//            Session session = createSession();
//
//            // Get the "beta" folder by path
//            Folder betaFolder = (Folder) session.getObjectByPath("/beta");
//
//            // Define the properties for the new documents
//            List<beta> offres = betaService.scrapeOffres(); // Assuming this method returns a list of beta objects
//            for (beta offre : offres) {
//                String name = offre.getTitre() + ".html";
//                String origine = offre.getOrigine();
//                String dateExp = offre.getDateExp();
//                String date = offre.getDate();
//                List<String> appelOffre = betaService.GetAppelOffre(offre.getLien());
//
//                // Create the document
//                Document document = null;
//                try {
//                    Map<String, Object> properties = new HashMap<>();
//                    properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
//                    properties.put(PropertyIds.NAME, name);
//                    properties.put(PropertyIds.CREATED_BY, origine);
//                    properties.put(PropertyIds.DESCRIPTION, dateExp);
//                    properties.put(PropertyIds.CREATION_DATE, date);
//
//                    for (String appel : appelOffre) {
//                        // Check if the "appel" is a PDF or DOCX
//                        if (isPDFOrDOCX(appel)) {
//                            // Replace the file extension from .docx to .pdf
//                            String fileName = name.replace(".docx", ".pdf").replace(".doc", ".pdf");
//
//                            // Set the content of the document with the PDF or DOCX
//                            try (InputStream docStream = new URL(appel).openStream()) {
//                                ContentStream docContentStream = session.getObjectFactory().createContentStream(
//                                        fileName, -1, "application/pdf", docStream);
//                                document = betaFolder.createDocument(properties, docContentStream, VersioningState.MAJOR);
//                                System.out.println("Created document: " + document.getName());
//                            } catch (IOException e) {
//                                // Handle IO exception
//                                e.printStackTrace();
//                            }
//                        } else if (appel != null) {
//                            // Check if the "appel" is a URL or plain text content
//                            if (appel.startsWith("http")) {
//                                // Set the content of the document with the URL content
//                                try (InputStream urlStream = new URL(appel).openStream()) {
//                                    String mimeType = URLConnection.guessContentTypeFromStream(urlStream);
//                                    ContentStream docContentStream = session.getObjectFactory().createContentStream(
//                                            name, -1, mimeType, urlStream);
//                                    document = betaFolder.createDocument(properties, docContentStream, VersioningState.MAJOR);
//                                    System.out.println("Created document: " + document.getName());
//                                } catch (IOException e) {
//                                    // Handle IO exception
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                // Set the content of the document as plain text
//                                try (InputStream contentStream = new ByteArrayInputStream(appel.getBytes())) {
//                                    ContentStream docContentStream = session.getObjectFactory().createContentStream(
//                                            name, -1, "text/plain", contentStream);
//                                    document = betaFolder.createDocument(properties, docContentStream, VersioningState.MAJOR);
//                                    System.out.println("Created document: " + document.getName());
//                                } catch (IOException e) {
//                                    System.err.println("Failed to create plain text document: " + name);
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                } catch (CmisContentAlreadyExistsException e) {
//                    System.err.println("The document already exists: " + name);
//                    e.printStackTrace();
//                } catch (CmisBaseException e) {
//                    // Handle CMIS exception
//                    System.err.println("Failed to create document: " + name);
//                    e.printStackTrace();
//                }
//            }
//            System.out.println("Documents have been uploaded to the 'beta' folder");
//        } catch (CmisConnectionException | IOException e) {
//            // Handle CMIS connection exception
//            e.printStackTrace();
//        }
//    }

    private boolean isPDFOrDOCX(String url) {
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.endsWith(".pdf") || lowerCaseUrl.endsWith(".docx") || lowerCaseUrl.endsWith(".doc");
    }

    private Session createSession() {
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
