package pro.tendermarket.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.tendermarket.Model.AppelOffre;
import pro.tendermarket.Service.Metadata;


import java.util.List;

@RestController
@RequestMapping("/api")
public class MetadataController {
    @Autowired
    private Metadata metadata;

    @GetMapping("/metadata")
    public List<AppelOffre> getAllDocumentsFromArmpFolder() {
        List<AppelOffre> documentInfos = metadata.getAllDocumentsFromFolder();


        return documentInfos;
    }

}
