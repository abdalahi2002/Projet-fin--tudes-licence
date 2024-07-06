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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class BetaService {

    private static final String BASE_URL = "https://www.beta.mr";
    private static final String OFFRE_LIST_URL = BASE_URL + "/beta/liste_offres/4";

    public List<Appeloffre> scrapeOffres() throws IOException, ParseException {
        List<Appeloffre> offres = new ArrayList<>();
        Document doc = Jsoup.connect(OFFRE_LIST_URL).get();
        Elements offreCards = doc.select(".post-card");
        for (Element offreCard : offreCards) {
            Appeloffre offre = new Appeloffre();
            Origine origine = new Origine();
            origine.setNom(offreCard.selectFirst(".post-card-content").text());
            offre.setOrigine(origine);
            offre.setTitre(offreCard.selectFirst(".post-card-heading a").text());
            offre.setLien(offreCard.selectFirst(".post-card-heading a").attr("href"));
            offre.setObjet(offreCard.selectFirst(".card-badge").text().trim());
            offre.setDatePub(scrapeDate(offre.getLien()));
            Documents document = new Documents();
            document.setAppelOffre(GetAppelOffre(offre.getLien()));
            offre.setDocument(document);
            offres.add(offre);
        }
        return offres;
    }
    public List<String> GetAppelOffre(String lien) throws IOException {
        List<String> appelOffre = new ArrayList<>();
        Document doc = Jsoup.connect(lien).get();

        Elements linkElements = doc.selectFirst(".col-lg-12").select("a");
        if (linkElements != null && !linkElements.isEmpty()) {
            for (Element linkElement : linkElements) {
                String url = linkElement.attr("href");
                if (isTextualLink(url)) {
                    appelOffre.add(url);
                }
            }
        } else {
            Elements elements = doc.select(".col-lg-12");
            StringBuilder plainTextBuilder = new StringBuilder();

            for (Element element : elements) {
                if (element.tagName().equals("p")) {
                    plainTextBuilder.append(element.text()).append("\n");
                } else if (element.tagName().equals("ol")) {
                    Elements listItems = element.select("li");
                    for (Element listItem : listItems) {
                        plainTextBuilder.append("• ").append(listItem.text()).append("\n");
                    }
                    plainTextBuilder.append("\n");
                } else if (element.tagName().equals("ul")) {
                    Elements listItems = element.select("li");
                    for (Element listItem : listItems) {
                        plainTextBuilder.append(". ").append(listItem.text()).append("\n");
                    }
                    plainTextBuilder.append("\n");
                }
            }

            if (plainTextBuilder.length() > 0) {
                appelOffre.add(plainTextBuilder.toString());
            }
        }

        return appelOffre;
    }

    private boolean isTextualLink(String url) {
        String lowerCaseUrl = url.toLowerCase();
        return lowerCaseUrl.endsWith(".pdf") || lowerCaseUrl.endsWith(".docx") || lowerCaseUrl.endsWith(".doc");
    }



    public LocalDate scrapeDate(String lien) throws IOException, ParseException {
        Document doc = Jsoup.connect(lien).get();
        Elements offreRows = doc.select(".col-6 span");
        if (!offreRows.isEmpty()) {
            Element offreRow = offreRows.first();
            String dateText = offreRow.text().trim();
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE);
            Date date = inputFormat.parse(dateText);

            // Convert to LocalDate without time
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return localDate;
        }
        return null; // Ou lancez une exception appropriée pour indiquer l'absence de date
    }

    private String getFirstImage(String lien) throws IOException {
        Document doc = Jsoup.connect(lien).get();
        Element imgElement = doc.selectFirst(".col-lg-12 img");
        if (imgElement != null) {
            String imageUrl = imgElement.attr("src");
            return imageUrl;
        }
        return null;
    }



}
