package pro.tendermarket.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.tendermarket.Model.documents;
import pro.tendermarket.Service.Documents;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pdf")
@CrossOrigin(origins = "*")
public class documentController {

    @GetMapping("/{objet}")
    public ResponseEntity<Map<String, String>> getDocumentFromArmpFolder(@PathVariable String objet) {
        Documents document = new Documents();
        try {


            documents doc = document.getDocumentAsBase64(objet);

            if (doc != null && doc.getFile() != null) {
                String base64File = doc.getFile();

                Map<String, String> response = new HashMap<>();
                response.put("base64File", base64File);
                response.put("objet", objet);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
