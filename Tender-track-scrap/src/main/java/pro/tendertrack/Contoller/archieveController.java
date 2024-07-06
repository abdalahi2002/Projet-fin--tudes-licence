package pro.tendertrack.Contoller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.tendertrack.Service.archieve;
import pro.tendertrack.model.Appeloffre;

import java.io.IOException;
import java.util.List;
@RestController
public class archieveController {
    @Autowired
    private archieve archieve;

    @GetMapping("/archieve")
    public List<Appeloffre> scrapeOffres()  throws IOException {
        return archieve.scrapeOffres();

    }

}
