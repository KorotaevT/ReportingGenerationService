package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.repository.UserRepository;
import cs.vsu.ReportingGenerationService.util.AuthenticationRequest;
import cs.vsu.ReportingGenerationService.util.AuthenticationResponse;
import cs.vsu.ReportingGenerationService.util.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Authority authority = new Authority(Role.GUEST);
        var user = User.builder()
                .registrationDate(LocalDate.now())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .authorities(List.of(authority))
                .build();
        authority.setUser(user);
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var user =  repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }
}
