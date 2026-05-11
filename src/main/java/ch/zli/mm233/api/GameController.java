package ch.zli.mm233.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {

    @GetMapping("/state")
    public String state() {
        return "Secret Hitler API is running!";
    }
}
