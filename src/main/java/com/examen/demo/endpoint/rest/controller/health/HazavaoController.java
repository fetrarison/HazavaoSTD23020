package com.examen.demo.endpoint.rest.controller.health;

import com.examen.demo.service.ChatGptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hazavao")
public class HazavaoController {

    private final ChatGptService chatGptService;

    public HazavaoController(ChatGptService chatGptService) {
        this.chatGptService = chatGptService;
    }

    @GetMapping
    public String definirMot(@RequestParam String teny) {
        return chatGptService.getDefinitionInMalagasy(teny);
    }
}
