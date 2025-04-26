package home.example.board.controller.api.bot;

import home.example.board.DTO.botLoginDTO.BotLoginRequestDTO;
import home.example.board.DTO.botLoginDTO.BotLoginResponseDTO;
import home.example.board.service.bot.BotLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotAuthAPI {

    private final BotLoginService botLoginService;

    @Autowired
    public BotAuthAPI(BotLoginService botLoginService) {
        this.botLoginService = botLoginService;
    }

    @PostMapping("/api/bot/login")
    public ResponseEntity<BotLoginResponseDTO> botLogin(BotLoginRequestDTO botLoginRequestDTO) {
        try {
            String token = botLoginService.generateLoginToken(botLoginRequestDTO);
            return ResponseEntity.ok(new BotLoginResponseDTO(token));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(new BotLoginResponseDTO("Access denied: " + e.getMessage()));
        }
    }

}
