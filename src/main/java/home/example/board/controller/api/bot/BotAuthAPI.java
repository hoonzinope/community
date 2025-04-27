package home.example.board.controller.api.bot;

import home.example.board.DTO.botLoginDTO.BotLoginRequestDTO;
import home.example.board.DTO.botLoginDTO.BotLoginResponseDTO;
import home.example.board.service.bot.BotLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Bot Login API", description = "API for bot login")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "Success response",
                                                    value = "{\"token\" : \"jwt_token_here\"}"
                                            )
                                    }
                            )
                    }),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                                    name = "Error response",
                                                    value = "{\"result\" : \"fail\", \"message\" : \"Access denied\"}"
                                            )
                                    }
                            )
                    })
    })
    @PostMapping("/api/bot/login")
    public ResponseEntity<BotLoginResponseDTO> botLogin(
            @RequestBody(description = "Bot login request body",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Request body",
                                    value = "{\"user_name\" : \"bot_user\", \"user_pw\" : \"bot_password\"}"
                            )
                    }
            ))
            @org.springframework.web.bind.annotation.RequestBody BotLoginRequestDTO botLoginRequestDTO) {
        try {
            String token = botLoginService.generateLoginToken(botLoginRequestDTO);
            return ResponseEntity.ok(new BotLoginResponseDTO(token));
        } catch (IllegalAccessException e) {
            return ResponseEntity.status(403).body(new BotLoginResponseDTO("Access denied: " + e.getMessage()));
        }
    }

}
