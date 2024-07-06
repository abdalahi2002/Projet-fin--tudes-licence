package pro.tendermarket.Service;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.stereotype.Service;
import pro.tendermarket.ApiConnection;
import pro.tendermarket.Model.AppelOffre;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Metadata {
    private static final int PAGE_SIZE = 20; // Nombre d'éléments par page

    public List<AppelOffre> getAllDocumentsFromFolder() {
        List<AppelOffre> documentList = new ArrayList<>();
        ApiConnection api = new ApiConnection();
        Session session = api.createSession();

        try {
            // Obtenir le dossier "armp" par son chemin
            Folder armpFolder = (Folder) session.getObjectByPath("/Appels d'offres");

            if (armpFolder != null) {
                // Définir les paramètres de pagination
                int pageSize = 20; // Nombre de documents à récupérer par page
                int skipCount = 0; // Nombre de documents à ignorer au début

                OperationContext operationContext = session.createOperationContext();
                operationContext.setMaxItemsPerPage(pageSize);

                boolean hasMoreItems = true;

                while (hasMoreItems) {
                    // Récupérer les documents par lot en utilisant la pagination
                    ItemIterable<CmisObject> children = armpFolder.getChildren(operationContext);
                    children.skipTo(skipCount); // Ignorer les documents précédents

                    for (CmisObject child : children) {
                        if (child instanceof Document) {
                            Document document = (Document) child;
                            String objet = document.getName();
                            String origine = document.getPropertyValue("cm:author");
                            String Titre = document.getPropertyValue("cm:title");
                            String dateString = document.getPropertyValue("cm:description");
                            LocalDate date = null;

                            if (dateString != null) {
                                try {
                                    date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                } catch (DateTimeParseException e) {
                                    // Handle the case where the dateString is not in the expected format
                                    // You can log an error or handle it in any other appropriate way
                                    e.printStackTrace();
                                }
                            }

                            documentList.add(new AppelOffre(Titre, objet, date, origine));
                        }
                    }

                    // Vérifier si d'autres documents sont disponibles pour la pagination
                    if (!children.getHasMoreItems()) {
                        hasMoreItems = false;
                    } else {
                        skipCount += pageSize;
                    }
                }


            }
        } catch (CmisObjectNotFoundException e) {
            e.printStackTrace();
        } finally {
            session.clear();
        }

        return documentList;
    }




}