package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.dto.UserDTO;
import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.repository.AuthorityRepository;
import cs.vsu.ReportingGenerationService.repository.ReportRepository;
import cs.vsu.ReportingGenerationService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    ReportRepository reportRepository;

    public Optional<List<User>> getAuthRequests() {
        return userRepository.findAllByAuthoritiesRole(Role.GUEST);
    }

    public Optional<List<User>> getUsers() {
        return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
    }

    public Optional<List<Report>> getReports() {return Optional.ofNullable(reportRepository.findAll());}

    public Optional<Report> getReportById(Long id) {return Optional.ofNullable(reportRepository.findById(id));}

    @Transactional
    public Optional<List<User>> approveAuthRequest(User user) {
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
    public Optional<List<User>> rejectAuthRequest(User user) {
        Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(user.getId());
        if (authorityOptional.isPresent()) {
            if (authorityOptional.get().getRole() == Role.GUEST) {
                userRepository.deleteById(user.getId());
                authorityRepository.deleteFirstByUserId(user.getId());
            }
        }
        return userRepository.findAllByAuthoritiesRole(Role.GUEST);
    }

    @Transactional
    public Optional<List<User>> changeUserRole(User user, Role role) {
        User approvedUser = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(approvedUser.getId());

        if (authorityOptional.isPresent()) {
            Authority authority = authorityOptional.get();
            authority.setRole(role);
            authorityRepository.save(authority);
        } else {
            throw new RuntimeException("У пользователя нет роли");
        }
        return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
    }

    @Transactional
    public Optional<List<User>> deleteUser(User user) {
        Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(user.getId());
        if (authorityOptional.isPresent()) {
            userRepository.deleteById(user.getId());
            authorityRepository.deleteFirstByUserId(user.getId());
        }
        return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
    }

    public void createReport(Report report) {
        reportRepository.save(report);
    }

    public void changeReport(Report updatedReport) {
        Optional<Report> existingReportOptional = Optional.ofNullable(reportRepository.findById(updatedReport.getId()));

        if (existingReportOptional.isPresent()) {
            Report existingReport = existingReportOptional.get();
            existingReport.setName(updatedReport.getName());
            existingReport.setUrl(updatedReport.getUrl());
            existingReport.setUsername(updatedReport.getUsername());
            existingReport.setPassword(updatedReport.getPassword());
            existingReport.setFields(updatedReport.getFields());
            existingReport.setQuery(updatedReport.getQuery());
            reportRepository.save(existingReport);
        }
    }


    @Transactional
    public void deleteReportById(Long id) {
        reportRepository.deleteById(id);
    }
}
