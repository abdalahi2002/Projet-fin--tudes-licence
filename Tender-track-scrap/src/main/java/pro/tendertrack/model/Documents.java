package pro.tendertrack.model;

import java.util.List;

public class Documents {

    private String chemin;

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    private List<String> appelOffre;

    public List<String> getAppelOffre() {
        return appelOffre;
    }

    public void setAppelOffre(List<String> appelOffre) {
        this.appelOffre = appelOffre;
    }
}
