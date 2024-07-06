package pro.tendertrack.Contoller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.tendertrack.Service.TestService;
import pro.tendertrack.model.Appeloffre;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public List<Appeloffre> scrapeOffres()  throws IOException {
        return testService.scrapeOffres();

    }

}
