package cs.vsu.ReportingGenerationService.service;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.exception.AuthorityNotFoundException;
import cs.vsu.ReportingGenerationService.exception.ReportGenerationException;
import cs.vsu.ReportingGenerationService.exception.ReportNotFoundException;
import cs.vsu.ReportingGenerationService.exception.UserNotFoundException;
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
        try {
            return userRepository.findAllByAuthoritiesRole(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при получении запросов на авторизацию", e);
        }
    }

    public Optional<List<User>> getUsers() {
        try {
            return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при получении списка пользователей", e);
        }
    }

    public Optional<List<Report>> getReports() {
        try {
            return Optional.ofNullable(reportRepository.findAll());
        } catch (Exception e) {
            throw new ReportGenerationException("Ошибка при получении отчетов", e);
        }
    }

    public Optional<Report> getReportById(Long id) {
        try {
            return reportRepository.findById(id);
        } catch (Exception e) {
            throw new ReportNotFoundException("Ошибка при получении отчета по ID: " + id, e);
        }
    }

    public Optional<List<ReportRequest>> getReportRequests() {
        try {
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
        } catch (Exception e) {
            throw new ReportGenerationException("Ошибка при получении запросов на отчет", e);
        }
    }

    @Transactional
    public Optional<List<User>> approveAuthRequest(User user) {
        try {
            User approvedUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + user.getId()));
            Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(approvedUser.getId());

            if (authorityOptional.isPresent()) {
                Authority authority = authorityOptional.get();
                authority.setRole(Role.USER);
                authorityRepository.save(authority);
            } else {
                throw new AuthorityNotFoundException("У пользователя нет роли: " + approvedUser.getId());
            }
            return userRepository.findAllByAuthoritiesRole(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при одобрении запроса на авторизацию", e);
        }
    }

    @Transactional
    public Optional<List<User>> rejectAuthRequest(User user) {
        try {
            Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(user.getId());
            if (authorityOptional.isPresent()) {
                if (authorityOptional.get().getRole() == Role.GUEST) {
                    userRepository.deleteById(user.getId());
                    authorityRepository.deleteFirstByUserId(user.getId());
                }
            }
            return userRepository.findAllByAuthoritiesRole(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при отклонении запроса на авторизацию", e);
        }
    }

    @Transactional
    public Optional<List<User>> changeUserRole(User user, Role role) {
        try {
            User approvedUser = userRepository.findById(user.getId())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + user.getId()));
            Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(approvedUser.getId());

            if (authorityOptional.isPresent()) {
                Authority authority = authorityOptional.get();
                authority.setRole(role);
                authorityRepository.save(authority);
            } else {
                throw new AuthorityNotFoundException("У пользователя нет роли: " + approvedUser.getId());
            }
            return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при изменении роли пользователя", e);
        }
    }

    @Transactional
    public Optional<List<User>> deleteUser(User user) {
        try {
            Optional<Authority> authorityOptional = authorityRepository.findFirstByUserId(user.getId());
            if (authorityOptional.isPresent()) {
                userRepository.deleteById(user.getId());
                authorityRepository.deleteFirstByUserId(user.getId());
            }
            return userRepository.findAllByAuthoritiesRoleNot(Role.GUEST);
        } catch (Exception e) {
            throw new AuthorityNotFoundException("Ошибка при удалении пользователя", e);
        }
    }

    public void createReport(Report report) {
        try {
            reportRepository.save(report);
        } catch (Exception e) {
            throw new ReportGenerationException("Ошибка при создании отчета", e);
        }
    }

    public void changeReport(Report updatedReport) {
        try {
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
            } else {
                throw new ReportNotFoundException("Отчет не найден: " + updatedReport.getId());
            }
        } catch (Exception e) {
            throw new ReportGenerationException("Ошибка при изменении отчета", e);
        }
    }

    @Transactional
    public void deleteReportById(Long id) {
        try {
            reportRepository.deleteById(id);
        } catch (Exception e) {
            throw new ReportNotFoundException("Ошибка при удалении отчета по ID: " + id, e);
        }
    }

}