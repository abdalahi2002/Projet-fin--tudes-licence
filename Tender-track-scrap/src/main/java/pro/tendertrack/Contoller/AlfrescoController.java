package pro.tendertrack.Contoller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.tendertrack.Repository.TestRepository;
import pro.tendertrack.Repository.armpRepository;
import pro.tendertrack.Repository.betaRepository;

import java.io.IOException;


@RequestMapping("/api")
@RestController
public class AlfrescoController {

    @Autowired
    private armpRepository armpRepository;

    @Autowired
    private betaRepository betaRepository;

    @PostMapping(value = "/uploads")

    public void upload() throws IOException {
        armpRepository.uploadFiles();
    }

//    @PostMapping("/uploadbeta")
//    public void uploads() throws IOException {
//        betaRepository.uploadFiles();
//    }

    @Autowired
    private TestRepository testRepository;
    @PostMapping("/test")
    public void test() throws IOException {
        testRepository.uploadFiles();
    }
}
