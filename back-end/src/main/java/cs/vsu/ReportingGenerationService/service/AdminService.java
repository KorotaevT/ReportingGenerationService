package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.repository.AuthorityRepository;
import cs.vsu.ReportingGenerationService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    public Optional<List<User>> getAuthRequests(){
        return userRepository.findAllByAuthoritiesRole(Role.GUEST);
    }

    @Transactional
    public Optional<List<User>> approveAuthRequest(User user){
        User approvedUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(approvedUser.getId());

        if (authorityOptional.isPresent()) {
            Authority authority = authorityOptional.get();
            authority.setRole(Role.USER);
            authorityRepository.save(authority);
        } else {
            throw new RuntimeException("У пользователя нет роли");
        }
        return userRepository.findAllByAuthoritiesRole(Role.GUEST);
    }

    @Transactional
    public Optional<List<User>> rejectAuthRequest(User user){
        //TODO
        return Optional.of(new ArrayList<>());
    }

}
