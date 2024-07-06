package pro.tendertrack.model;

import java.time.LocalDate;

public class Appeloffre {
    private String Titre;


    private String objet ;

    private LocalDate DatePub;

    private Origine origine;

    private Documents document;


    private String Lien;





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

    public Origine getOrigine() {
        return origine;
    }

    public void setOrigine(Origine origine) {
        this.origine = origine;
    }

    public Documents getDocument() {
        return document;
    }

    public void setDocument(Documents document) {
        this.document = document;
    }


    public String getLien() {
        return Lien;
    }

    public void setLien(String lien) {
        Lien = lien;
    }
}
