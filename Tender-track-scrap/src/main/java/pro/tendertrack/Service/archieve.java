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
public class archieve {
    private static final String BASE_URL = "https://armp.mr";
    private static final String OFFRE_LIST_URL = BASE_URL + "/2022/";

    public List<Appeloffre> scrapeOffres() throws IOException {
        List<Appeloffre> offres = new ArrayList<>();

        // Se connecter à chaque page
        for (int page = 1; page <= 5; page++) {
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


                try {
                    Origine origine = new Origine();
                    origine.setNom(getOrigine(offre.getLien()));
                    origine.setImg(offreRow.selectFirst(".post_thumbnail img").attr("src"));
                    offre.setOrigine(origine);
                } catch (NullPointerException e) {
                    Origine origine = new Origine();
                    origine.setNom("Origine non trouvée");
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
                    break; // Ajout de la ligne pour sortir de la boucle après avoir trouvé le premier lien
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
            }
        }

        return sb.toString();
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
            String cryptedLink = offerRow.substring(startIndex, endIndex);
            String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");
            String decodedLink = URLDecoder.decode(modifiedString, "UTF-8");

            return "https://" + decodedLink + ".pdf";
        }

        String extension = "";
        endIndex = offerRow.indexOf(".", startIndex); // find the first occurrence of "." after the start index
        if (endIndex != -1) {
            extension = offerRow.substring(endIndex + 1, endIndex + 4);
        }

        switch (extension) {
            case "doc":
            case "docx":
            case "xls":
            case "xlsx":
                String cryptedLink = offerRow.substring(startIndex, endIndex);
                String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");
                String decodedLink = URLDecoder.decode(modifiedString, "UTF-8");

                return "https://" + decodedLink + "." + extension;
            default:
                return "Lien non trouvé";
        }
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
        int endIndex = structure.indexOf("-", startIndex);
        int SecondEndIndex = structure.indexOf("%20%", startIndex);
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
        if (SecondEndIndex != -1 && endIndex != -1) {
            String SecondorigineText = structure.substring(startIndex + 1, SecondEndIndex);
            try {
                String SdecryptedOrigine = URLDecoder.decode(SecondorigineText, "UTF-8").replaceAll("\\\\", "");
                return SdecryptedOrigine;

            } catch (UnsupportedEncodingException e) {
                System.err.println("Unsupported encoding: UTF-8");
                e.printStackTrace();
            }
        }
        return "Origine non trouvée";
    }


}