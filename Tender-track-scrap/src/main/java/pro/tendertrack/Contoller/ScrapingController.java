package pro.tendertrack.Contoller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.tendertrack.Service.BetaService;
import pro.tendertrack.Service.armpService;
import pro.tendertrack.model.Appeloffre;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/offre")
public class ScrapingController {
    @Autowired
    private armpService armpservice;

    @GetMapping("/armp")
    public List<Appeloffre> scrapeOffres()  throws IOException {
        return armpservice.scrapeOffres();

    }

    @Autowired
    private BetaService betaservice;
    @GetMapping("/beta")
    public List<Appeloffre> scrapingOffres() throws IOException, ParseException {
        return betaservice.scrapeOffres();
    }


}
