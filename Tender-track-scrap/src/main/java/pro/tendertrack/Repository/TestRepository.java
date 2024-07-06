package pro.tendertrack.Repository;


import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.springframework.stereotype.Service;
import pro.tendertrack.ApiConnection;
import pro.tendertrack.Service.TestService;
import pro.tendertrack.model.Appeloffre;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class TestRepository {

    public void uploadFiles() {
        try {
            TestService test = new TestService();
            ApiConnection api = new ApiConnection();
            // Create a CMIS session
            Session session = api.createSession();

            // Get the "armp" folder by path
            Folder armpFolder = (Folder) session.getObjectByPath("/Appels d'offres");

            // Create the "images" folder if it doesn't exist
            Folder imagesFolder = (Folder) session.getObjectByPath("/Appels d'offres/images");

            // Define the properties for the new documents
            List<Appeloffre> offres = test.scrapeOffres(); // Assuming this method returns a list of Appeloffre objects
            int suffixe = 1; // Initialize the suffix with 1
            for (Appeloffre offre : offres) {
                String origine = offre.getOrigine().getNom();
                String objet = offre.getObjet();
                LocalDate date = offre.getDatePub();
                String docUrl = offre.getDocument().getChemin();
                String imageUrl = offre.getOrigine().getImg();
                String title = offre.getTitre();
                String name = objet + "-" + suffixe + docUrl.substring(docUrl.lastIndexOf("/") + 1);
                name.replaceAll("[/\\\\*<>:|\"?]", "-");
                // Increment the suffix for the next document
                suffixe++;
                Document document = null;
                try {
                    Map<String, Object> properties = new HashMap<>();
                    properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                    properties.put(PropertyIds.NAME, name);
                    List<String> secondary = new ArrayList<>();
                    secondary.add("P:cm:titled");
                    secondary.add("P:cm:author");
                    properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondary);
                    properties.put("cm:title", title);
                    properties.put("cm:author", origine);

                    // Convert LocalDate to Date
                    Date dateObject = java.sql.Date.valueOf(date);

                    // Format the date into a CMIS compatible format
                    SimpleDateFormat cmisDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateAsString = cmisDateFormat.format(dateObject);

                    properties.put("cm:description", dateAsString);

                    // Set the content of the document (for the PDF or DOCX file)
                    try (InputStream docStream = new URL(docUrl).openStream()) {
                        String fileExtension = getFileExtension(docUrl);
                        String fileMimeType = getFileMimeType(fileExtension);

                        ContentStream docContentStream = session.getObjectFactory().createContentStream(
                                name + fileExtension, -1, fileMimeType, docStream);

                        document = armpFolder.createDocument(properties, docContentStream, VersioningState.MAJOR);
                    } catch (IOException e) {
                        System.err.println("Failed to read document stream: " + name);
                        continue;
                    }

                    // Check if an image with the same name already exists in the "images" folder
                    boolean imageExists = false;
                    try {
                        session.getObjectByPath(imagesFolder.getPath() + "/" + offre.getOrigine().getNom() + getImageExtension(imageUrl));
                        imageExists = true;
                    } catch (CmisObjectNotFoundException e) {
                        // Image does not exist
                    }

                    // Upload the associated image as a separate document in the "images" folder
                    if (imageUrl != null && !imageUrl.isEmpty() && !imageExists) {
                        try (InputStream imgStream = new URL(imageUrl).openStream()) {
                            String imageExtension = getImageExtension(imageUrl);
                            String imageMimeType = getImageMimeType(imageExtension);
                            String imageName;

                            // Check if the origin starts with "ministere"
                            if (offre.getOrigine().getNom().toLowerCase().startsWith("ministere")) {
                                imageName = "ministere" + imageExtension; // Use a generic image name for all ministères
                            } else {
                                imageName = offre.getOrigine().getNom() + imageExtension; // Use the original image name
                            }

                            // Create a new document for the image
                            Map<String, Object> imageProperties = new HashMap<>();
                            imageProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                            imageProperties.put(PropertyIds.NAME, imageName);

                            ContentStream imgContentStream = session.getObjectFactory().createContentStream(
                                    imageName, -1, imageMimeType, imgStream);

                            Document imageDocument = imagesFolder.createDocument(imageProperties, imgContentStream, VersioningState.MAJOR);
                            System.out.println("Created image document: " + imageDocument.getName() + " [" + imageDocument.getId() + "]");
                        } catch (IOException e) {
                            System.err.println("Failed to read image stream: " + name);
                            e.printStackTrace();
                        }
                    } else if (imageExists) {
                        System.out.println("Image already exists: " + offre.getOrigine().getNom() + getImageExtension(imageUrl));
                    }

                } catch (CmisBaseException e) {
                    System.err.println("Failed to create document: " + name);
                    e.printStackTrace();
                    continue;
                }

                System.out.println("Created document: " + document.getName() + " [" + document.getId() + "]");
            }
            System.out.println("Documents have been uploaded to the 'armp' folder");
        } catch (CmisUnauthorizedException e) {
            System.err.println("Access denied. Check your credentials.");
            e.printStackTrace();
        } catch (CmisObjectNotFoundException e) {
            System.err.println("The 'armp' folder could not be found.");
            e.printStackTrace();
        } catch (CmisBaseException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }


    private String getImageExtension(String imageUrl) {
        String extension = "";
        int lastIndex = imageUrl.lastIndexOf(".");
        if (lastIndex != -1) {
            extension = imageUrl.substring(lastIndex);
        }
        return extension;
    }

    private String getImageMimeType(String imageExtension) {
        switch (imageExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
    private String getFileExtension(String fileName) {
        String extension = "";
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex != -1) {
            extension = fileName.substring(lastIndex);
        }
        return extension;
    }

    private String getFileMimeType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".pdf":
                return "application/pdf";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".doc":
                return "application/msword";
            case ".xls":
                return "application/vnd.ms-excel";
            default:
                return "application/octet-stream";
        }
    }
}
