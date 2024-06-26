package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.model.ReportRequest;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.repository.AuthorityRepository;
import cs.vsu.ReportingGenerationService.repository.ReportRepository;
import cs.vsu.ReportingGenerationService.repository.ReportRequestRepository;
import cs.vsu.ReportingGenerationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final ReportRequestRepository reportRequestRepository;

    private final ReportRepository reportRepository;

    public Optional<List<User>> getAuthRequests() {
        return userRepository.findAllByAuthoritiesRole(Role.GUEST);
    }

    public Optional<List<User>> getUsers() {
        return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
    }

    public Optional<List<Report>> getReports() {return Optional.ofNullable(reportRepository.findAll());}


    public Optional<Report> getReportById(Long id) {return reportRepository.findById(id);}

    public Optional<List<ReportRequest>> getReportRequests() {
        List<ReportRequest> reportRequests = reportRequestRepository.findAllByOrderByRequestTimeDesc();
        List<ReportRequest> modifiedReportRequests = reportRequests.stream()
                .peek(reportRequest -> {
                    Report modifiedReport = new Report();
                    modifiedReport.setId(reportRequest.getReport().getId());
                    modifiedReport.setName(reportRequest.getReport().getName());
                    reportRequest.setReport(modifiedReport);
                })
                .collect(Collectors.toList());
        return Optional.of(modifiedReportRequests);
    }

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
        Optional<Report> existingReportOptional = reportRepository.findById(updatedReport.getId());

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