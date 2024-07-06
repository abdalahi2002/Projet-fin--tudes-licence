package pro.tendermarket.Model;

import java.time.LocalDate;


public class AppelOffre {

    private String Titre;


    private String objet;

    private LocalDate DatePub;

    private String origine;

    public AppelOffre(String titre, String objet, LocalDate datePub, String origine) {
        Titre = titre;
        this.objet = objet;
        DatePub = datePub;
        this.origine = origine;
    }

    public AppelOffre(byte[] documentContent) {
    }

    public String getTitre() {
        return Titre;
    }

    public void setTitre(String titre) {
        Titre = titre;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public LocalDate getDatePub() {
        return DatePub;
    }

    public void setDatePub(LocalDate datePub) {
        DatePub = datePub;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {this.origine = origine;
    }

}