package nl.appsource.ok;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OkController {

    @RequestMapping("/")
    String home() {
        return "OK";
    }

}