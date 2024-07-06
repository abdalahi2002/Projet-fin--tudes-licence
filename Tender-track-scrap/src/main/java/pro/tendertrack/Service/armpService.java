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
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class armpService {

    private static final String BASE_URL = "https://armp.mr";
    private static final String OFFRE_LIST_URL = BASE_URL + "/avis-dappel-publics-avis-dattribution-plans-et-avis-generaux-autres/";


    public List<Appeloffre> scrapeOffres() throws IOException {
        List<Appeloffre> offres = new ArrayList<>();
        Document doc = Jsoup.connect(OFFRE_LIST_URL).get();
        Elements offreRows = doc.select(".posts-data-table tbody tr");

        for (int i = 0; i < 100 && i < offreRows.size(); i++) {
            Element offreRow = offreRows.get(i);

            Appeloffre offre = new Appeloffre();
            try {
                offre.setTitre(offreRow.selectFirst("td a").text());
            } catch (NullPointerException e) {
                offre.setTitre("Titre non trouvé");
            }

            try {
                offre.setObjet(offreRow.select("td").get(3).text().trim());
            } catch (NullPointerException e) {
                offre.setObjet("Objet non trouvé");
            }

            try {
                offre.setLien(offreRow.selectFirst("td a").attr("href"));
            } catch (NullPointerException e) {
                offre.setLien("Lien non trouvé");
            }

            try {
                String dateText = offreRow.select("td").get(4).text().trim();
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
                Date date = inputFormat.parse(dateText);

                // Convert to LocalDate without time
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                offre.setDatePub(localDate);
            } catch (NullPointerException | ParseException e) {
                offre.setDatePub(null);
            }

            try {
                Origine origine = new Origine();
                origine.setNom(offreRow.select("td").get(1).text().trim());
                origine.setImg(offreRow.selectFirst("td img").attr("src"));
                offre.setOrigine(origine);
            } catch (NullPointerException e) {
                Origine origine = new Origine();
                origine.setNom("Origine non trouvée");
                origine.setImg("Lien non trouvé");
                offre.setOrigine(origine);
            }

            try {
                Documents document = new Documents();
                document.setChemin(getOffersDownloadLinks(offre.getLien()));
                offre.setDocument(document);
            } catch (NullPointerException e) {
                Documents document = new Documents();
                document.setChemin("Chemin non trouvé");
                offre.setDocument(document);
            }

            offres.add(offre);
        }

        return offres;
    }




    public String getOffersDownloadLinks(String Lien) throws IOException {
        StringBuilder sb = new StringBuilder();
        String decodedLien = null;
        try {
            decodedLien = URLDecoder.decode(Lien, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return sb.toString();
        }

        Document doc = Jsoup.connect(decodedLien).get();

        // Get links from first structure
        Elements offerRows = doc.select(".elfsight-widget");
        for (Element offerRow : offerRows) {
            Element lienElement = offerRow.selectFirst("a");
            if (lienElement != null) {
                String lien = lienElement.attr("href");
                sb.append(lien).append("");
            } else {
                String otherChoiceResult = otherChoice(offerRow.toString());
                if (!"Lien non trouvé".equals(otherChoiceResult)) {
                    sb.append(otherChoiceResult).append("");
                }
            }
        }

        // Get links from second structure
        Element lienElement = doc.selectFirst(".wpb_wrapper span a , .wpb_wrapper p strong a , .wpb_wrapper p a , .entry-content p strong a");
        if (lienElement != null) {
            String lien = lienElement.attr("href");
            sb.append(lien).append("");
        }

        return sb.toString();
    }
    public String otherChoice(String offerRow) {
        int startIndex = offerRow.toString().indexOf("armp.mr"); // find the first occurrence of "http"
        int endIndex = offerRow.toString().indexOf(".pdf", startIndex); // find the first occurrence of ".pdf" after the start index
        if (endIndex != -1) {
            String cryptedLink = offerRow.toString().substring(startIndex, endIndex);
            String modifiedString = cryptedLink.replaceAll("%5C%2F", "/");

            return "https://" + modifiedString + ".pdf";
        }
        return "Lien non trouvé";
    }


}