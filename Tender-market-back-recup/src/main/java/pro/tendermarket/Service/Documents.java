package pro.tendermarket.Service;


import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import pro.tendermarket.ApiConnection;
import pro.tendermarket.Model.documents;


import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class Documents {
    private final String documentLibraryPath = "/Appels d'offres";

    public documents getDocumentAsBase64(String documentName) {
        ApiConnection api = new ApiConnection();
        try {

            documents documents = new documents();

            Session session = api.createSession();
            Document document = getDocumentByName(session, documentName);
            if (document != null) {
                String documentBase64 = convertDocumentToBase64(document);
                documents.setChemin(document.getName());
                documents.setFile(documentBase64);
            }
            session.clear();

            return documents;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private Document getDocumentByName(Session session, String documentName) {
        try {
            Folder documentLibrary = (Folder) session.getObjectByPath(documentLibraryPath);
            ItemIterable<CmisObject> children = documentLibrary.getChildren();
            for (CmisObject child : children) {
                if (child instanceof Document && child.getName().equals(documentName)) {
                    return (Document) child;
                }
            }
        } catch (CmisObjectNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDocumentToBase64(Document document) {
        try {
            String base64File = null;

            String mimeType = document.getContentStreamMimeType();
            if (mimeType != null && (mimeType.equals("application/pdf") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || mimeType.equals("application/vnd.ms-excel"))) {
                ContentStream contentStream = document.getContentStream();
                if (contentStream != null) {
                    InputStream inputStream = contentStream.getStream();
                    byte[] fileContent = IOUtils.toByteArray(inputStream);
                    base64File = Base64.getEncoder().encodeToString(fileContent);
                    inputStream.close();
                }
            }

            return base64File;
        } catch (CmisBaseException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
