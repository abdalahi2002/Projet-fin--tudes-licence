package pro.tendertrack.Service;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import pro.tendertrack.model.Appeloffre;
import pro.tendertrack.model.Documents;
import pro.tendertrack.model.Origine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TestService {
    private static final String BASE_URL = "https://armp.mr";
    private static final String OFFRE_LIST_URL = BASE_URL + "/2012/";
    public List<Appeloffre> scrapeOffres() throws IOException {
        List<Appeloffre> offres = new ArrayList<>();

        // Se connecter à chaque page
        for (int page = 1; page <= 1; page++) {
            String pageUrl = (page == 1) ? OFFRE_LIST_URL : OFFRE_LIST_URL + "page/" + page;
            Document doc = Jsoup.connect(pageUrl).get();

            Elements offreRows = doc.select(".stm_post_info");
            for (Element offreRow : offreRows) {
                Appeloffre offre = new Appeloffre();
                try {
                    offre.setTitre(offreRow.selectFirst("h4").text());
                } catch (NullPointerException e) {
                    offre.setTitre("Titre non trouvé");
                }

                try {
                    offre.setObjet(offreRow.select(".post_cat span").text().trim());
                } catch (NullPointerException e) {
                    offre.setObjet("Objet non trouvé");
                }

                try {
                    offre.setLien(offreRow.selectFirst(".post_read_more a").attr("href"));
                } catch (NullPointerException e) {
                    offre.setLien("Lien non trouvé");
                }

                try {
                    String dateText = offreRow.select("li .post_date").text().trim();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
                    java.util.Date date = inputFormat.parse(dateText);

                    // Convert to LocalDate without time
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    offre.setDatePub(localDate);
                } catch (NullPointerException | ParseException e) {
                    offre.setDatePub(null);
                }

                // Vérifier l'origine
                try {
                    Origine origine = new Origine();
                    String origineNom = getOrigine(offre.getLien());
                    String realOrigine = getRealOrigine(origineNom);
                    if(realOrigine.toUpperCase().contains("TELECHARGER")){
                        realOrigine = "Autorité de Régulation des Marchés Publics (armp)";
                    }
                    origine.setNom(realOrigine);

                    origine.setImg(offreRow.selectFirst(".post_thumbnail img").attr("src"));
                    offre.setOrigine(origine);
                } catch (NullPointerException e) {
                    Origine origine = new Origine();
                    origine.setNom("Autorité de Régulation des Marchés Publics (armp)");
                    origine.setImg("Lien non trouvé");
                    offre.setOrigine(origine);
                }

                try {
                    Documents document = new Documents();
                    document.setChemin(getAllDocumentsLink(offre.getLien()));
                    offre.setDocument(document);
                } catch (NullPointerException e) {
                    Documents document = new Documents();
                    document.setChemin("Chemin non trouvé");
                    offre.setDocument(document);
                }




                offres.add(offre);
            }
        }
        return offres;
    }


    public String getAllDocumentsLink(String Lien) throws IOException {
        StringBuilder sb = new StringBuilder();
        Document doc = Jsoup.connect(Lien).get();

        // Get links from first structure
        Elements offerRows = doc.select(".elfsight-widget");
        for (Element offerRow : offerRows) {
            Element lienElement = offerRow.selectFirst("a");
            if (lienElement != null) {
                String lien = URLDecoder.decode(lienElement.attr("href"), "UTF-8");
                if (isSupportedExtension(lien)) {
                    sb.append(lien).append("");
                    return sb.toString(); // Retourne le premier lien trouvé et arrête la méthode
                }
            } else {
                String otherChoiceResult = DecryptLink(offerRow.toString());
                if (!"Lien non trouvé".equals(otherChoiceResult)) {
                    sb.append(otherChoiceResult).append("");
                }
            }
        }

        // Get links from second structure
        Element lienElement = doc.selectFirst(".wpb_wrapper a , .entry-content a ");
        if (lienElement != null) {
            String lien = URLDecoder.decode(lienElement.attr("href"), "UTF-8");
            if (isSupportedExtension(lien)) {
                sb.append(lien).append("");
                return sb.toString(); // Retourne le lien trouvé et arrête la méthode
            }
        }

        // Get links from alternative structure
        Element alternativeElement = doc.selectFirst("div .entry-content p a");
        if (alternativeElement != null) {
            String lien = alternativeElement.attr("href");
            sb.append(lien).append("");
            return sb.toString(); // Retourne le lien trouvé et arrête la méthode
        }

        return ""; // Retourne une chaîne vide si aucun lien n'est trouvé
    }

    public String otherChoice(String offerRow) {
        int startIndex = offerRow.indexOf("armp.mr"); // find the first occurrence of "armp.mr"
        int endIndex = offerRow.indexOf(".docx", startIndex); // find the first occurrence of ".docx" after the start index
        if (endIndex != -1) {
            String cryptedLink = offerRow.substring(startIndex, endIndex);
            String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");
            return "https://" + modifiedString;
        } else {
            return "Lien non trouvé";
        }
    }

    private boolean isSupportedExtension(String link) {
        String[] supportedExtensions = {".pdf", ".docx", ".doc", ".xls", ".xlsx"};
        for (String extension : supportedExtensions) {
            if (link.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }




    public String DecryptLink(String offerRow) throws UnsupportedEncodingException {
        int startIndex = offerRow.indexOf("armp.mr"); // find the first occurrence of "armp.mr"
        int endIndex = offerRow.indexOf(".pdf", startIndex); // find the first occurrence of ".pdf" after the start index

        if (endIndex != -1) {
            String cryptedLink = offerRow.substring(startIndex, endIndex + 4); // include ".pdf" in the substring
            String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");
            String decodedLink = URLDecoder.decode(modifiedString, "UTF-8");

            return "https://" + decodedLink;
        }

        endIndex = offerRow.indexOf(".docx", startIndex); // find the first occurrence of ".docx" after the start index

        if (endIndex != -1) {
            String cryptedLink = offerRow.substring(startIndex, endIndex + 5); // include ".docx" in the substring
            String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");
            String decodedLink = URLDecoder.decode(modifiedString, "UTF-8");

            return "https://" + decodedLink;
        }

        return "Lien non trouvé";
    }






    public String getOrigine(String lien) throws IOException {
        StringBuilder sb = new StringBuilder();
        String decodedLien = null;
        try {
            decodedLien = URLDecoder.decode(lien, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return sb.toString();
        }

        Document doc = Jsoup.connect(decodedLien).get();

        // Get links from first structure
        Elements offerRows = doc.select(".elfsight-widget");
        for (Element offerRow : offerRows) {
            String otherChoiceResult = decryptedOrigine(offerRow.toString());
            if (!"Lien non trouvé".equals(otherChoiceResult)) {
                sb.append(otherChoiceResult).append("");
            }
        }

        // Get text from second structure
        Element lienElement = doc.selectFirst(".wpb_wrapper p , .entry-content p span strong , .wpb_wrapper h3 , .wpb_wrapper h2 span");
        if (lienElement != null) {
            String text = lienElement.text();
            sb.append(text).append("");
        }

        return sb.toString();
    }


    public String decryptedOrigine(String structure) {
        int startIndex = structure.indexOf("name%22%3A%22");
        int endIndex = structure.indexOf("%20", startIndex);

        if (startIndex != -1 && endIndex != -1) {
            String origineText = structure.substring(startIndex + 13, endIndex);

            try {
                String decryptedOrigine = URLDecoder.decode(origineText, "UTF-8").replaceAll("\\\\", "");
                return decryptedOrigine;
            } catch (UnsupportedEncodingException e) {
                System.err.println("Unsupported encoding: UTF-8");
                e.printStackTrace();
            }
        }
        return "Origine non trouvée";
    }



    public String getRealOrigine(String origine ) {
        if (origine.contains("– ")) {
            origine = origine.replace("– ", "(").concat(")");
        } else if (origine.contains("- ")) {
            origine = origine.replace("- ", " (").concat(")");
        }else if(origine.contains("AVIS") || origine.isEmpty() ){
            origine = "Autorité de Régulation des Marchés Publics (armp)";
         } else {
            switch (origine) {
                case "ETER":
                    return "Etablissement des Travaux d’Entretien Routier (ETER)";
                case "SNDP":
                    return "Société Nationale de Distribution de Poisson (SNDP)";
                case "CNO":
                    return "centre national d'oncologie";

                case "APCM":
                    return "Agence de Gestion des Palais de Congrès de Mauritanie (APCM)";
                case "ANESP":
                    return "Agence Nationale d'Exécution et de Suivi des Projets (ANESP)";
                case "ANARPAM":
                    return "Agence Nationale de Recherches Géologiques et du Patrimoine Minier (ANARPAM)";
                case "ANRPTS":
                    return "Agence Nationale du Registre des Populations et des Titres Sécurisés (ANRPTS)";
                case "ARMP":
                    return "Autorité de Régulation des Marchés Publics (ARMP)";
                case "CNAM":
                    return "Caisse Nationale d'Assurance Maladie (CNAM)";
                case "CNC":
                    return "Centre National de Cardiologie (CNC)";
                case "CSA":
                    return "Commissariat à la Sécurité Alimentaire (CSA)";
                case "CNHy":
                    return "Commission Nationale des Hydrocarbures";
                case "DPEF":
                    return "Direction des Projets Education - Formation (DPEF)";
                case "IPN":
                    return "Institut Pédagogique National (IPN)";
                case "ERRT":
                    return "Établissement pour la Réhabilitation et la Rénovation de la ville deTintane (ERRT)";
                case "TAAZOUR":
                    return "Délégation Générale à la Solidarité Nationale et à la Lutte contre l'Exclusion (TAAZOUR)";
                case "ETR-ML":
                    return "Etablissement d’Exécution des Travaux Réalisés en Matériaux Locaux (ETR-ML)";
                case "MAADEN":
                    return "Maaden Mauritanie";
                case "MPN":
                    return "Marché au Poisson de Nouakchott (MPN)";
                case "MASEF":
                    return "Ministère de l'Action Sociale de l'Enfance et de la Famille (MASEF)";
                case "MEFP":
                    return "Ministère de l'Emploi et de la Formation Professionnelle (MEFP)";
                case "MEDD":
                    return "Ministère de l'Environnement et du Développement Durable (MEDD)";
                case "MET":
                    return "Ministère de l'Equipement et des Transports (MET)";
                case "MHA":
                    return "Ministère de l'Hydraulique et de l'Assainissement (MHA)";
                case "MIDEC":
                    return "Ministère de l'Intérieur et de la Décentralisation (MIDEC)";
                case "MCARP":
                    return "Ministère de la Culture de la Jeunesse des Sports et des Relations avec le Parlement (MCARP)";
                case "MS":
                    return "Ministère de la Santé";
                case "MTNIMA":
                    return "Ministère de la Transition Numérique de l'Innovation et de la Modernisation de l'Administration (MTNIMA)";
                case "MA":
                    return "Ministère de l’Agriculture (MA)";
                case "MENRSE":
                    return "Ministère de l’Education Nationale et de la Réforme du Système Educatif (MENRSE)";
                case "MHUAT":
                    return "Ministère de l’Habitat de l’Urbanisme et de l’Aménagement du Territoire (MHUAT)";
                case "MAIEO":
                    return "Ministère des Affaires Islamiques et de l'Enseignement Originel (MAIEO)";
                case "MPEM":
                    return "Ministère du Pétrole des Mines et de l’Énergie (MPEM)";
                case "OCO":
                    return "Office du Complexe Olympique (OCO)";
                case "ONAS":
                    return "Office National d'Assainissement (ONAS)";
                case "PNBA":
                    return "Parc National de Banc d'Arguin (PNBA)";
                case "PT":
                    return "Port de Tanit";
                case "MOUDOUN":
                    return "Projet d'Appui à la Décentralisation et au Développement des Villes Intermédiaires Productives (MOUDOUN)";
                case "PATAM":
                    return "Projet d'Appui à la Transformation des Produits Agricoles en Mauritanie (PATAM)";
                case "PEJ":
                    return "Projet d'Employabilité des Jeunes (PEJ)";
                case "PRODEFI":
                    return "Projet de Développement de Filières Inclusives (PRODEFI)";
                case "PADG":
                    return "Projet d’Appui aux Négociations des Projets Gaziers et Renforcement des Capacités Institutionnelles (PADG)";
                case "PEJ - BAD":
                    return "Projet d’Appui à l’Employabilité et à l’Insertion Socio-Économique des Jeunes Vulnérables (PEJ - BAD)";
                case "SENLS":
                    return "Secrétariat Exécutif National de Lutte Contre le SIDA (SENLS)";
                case "SMH":
                    return "Société Mauritanienne des Hydrocarbures (SMH)";
                case "SOMIR":
                    return "Société Mauritanienne des Industries de Rafinage (SOMIR)";
                case "SOMELEC":
                    return "Société Mauritaniènne d'Électricité (SOMELEC)";
                case "SONADER":
                    return "Société Nationale pour le Développement Rural (SONADER)";
                case "TDM":
                    return "Télédiffusion de Mauritanie (TDM)";
                case "Ru00e9gion":
                    return "Région de Nouakchott";

            }
        }

        return origine;
    }


}
