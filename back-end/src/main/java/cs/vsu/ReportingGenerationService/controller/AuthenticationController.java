package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.service.AuthenticationService;
import cs.vsu.ReportingGenerationService.service.JwtService;
import cs.vsu.ReportingGenerationService.util.AuthenticationRequest;
import cs.vsu.ReportingGenerationService.util.AuthenticationResponse;
import cs.vsu.ReportingGenerationService.util.RegisterRequest;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Контроллер аутентификации", description = "API для аутентификации и регистрации пользователей")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping("/registration")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя и возвращает токен авторизации и данные пользователя")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            AuthenticationResponse register = authenticationService.register(request);
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            register.getToken()
                    )
                    .header(
                            "Access-Control-Expose-Headers",
                            HttpHeaders.AUTHORIZATION
                    )
                    .body(
                            register.getUser()
                    );
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/authentication")
    @Operation(summary = "Аутентификация пользователя", description = "Аутентифицирует пользователя по логину и паролю и возвращает токен авторизации и данные пользователя")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            AuthenticationResponse authenticate = authenticationService.authenticate(request);
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authenticate.getToken()
                    )
                    .header(
                            "Access-Control-Expose-Headers",
                            HttpHeaders.AUTHORIZATION
                    )
                    .body(
                            authenticate.getUser()
                    );
        }catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validation")
    @Operation(summary = "Проверка токена", description = "Проверяет действительность токена авторизации")
    public ResponseEntity<?> validateToken(@RequestParam String token, @AuthenticationPrincipal User user){
        try {
            boolean isTokenValid = jwtService.isTokenValid(token, user);
            return ResponseEntity.ok(isTokenValid);
        }catch (ExpiredJwtException e) {
            return ResponseEntity.ok(false);
        }
    }

}