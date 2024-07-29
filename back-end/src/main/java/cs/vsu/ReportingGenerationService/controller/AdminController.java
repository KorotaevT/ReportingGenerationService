package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dto.UserDTO;
import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.Report;
import cs.vsu.ReportingGenerationService.model.ReportRequest;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Контроллер администрирования", description = "API для управления пользователями, отчетами и запросами на авторизацию")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/getAuthRequests")
    @Operation(summary = "Получить запросы на авторизацию", description = "Возвращает список запросов на авторизацию")
    public ResponseEntity<List<User>> getAuthRequests() {
        Optional<List<User>> userList = adminService.getAuthRequests();
        return ResponseEntity.ok(userList.orElseGet(ArrayList::new));
    }

    @GetMapping("/getUsers")
    @Operation(summary = "Получить пользователей", description = "Возвращает список пользователей с их ролями")
    public ResponseEntity<List<UserDTO>> getUsers() {
        Optional<List<User>> userList = adminService.getUsers();
        List<UserDTO> userDTOsList = new ArrayList<>();

        userList.ifPresent(users -> {
            for (User user : users) {
                Authority authority = (Authority) user.getAuthorities().stream().findFirst().orElseThrow();
                Role role = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(user)
                        .role(role)
                        .build();
                userDTOsList.add(userDTO);
            }
        });
        return ResponseEntity.ok(userDTOsList);
    }

    @GetMapping("/getReports")
    @Operation(summary = "Получить отчеты", description = "Возвращает список отчетов")
    public ResponseEntity<List<Report>> getReports() {
        List<Report> reportList = adminService.getReports().orElseGet(ArrayList::new);
        return ResponseEntity.ok(reportList);
    }

    @GetMapping("/getReportById/{id}")
    @Operation(summary = "Получить отчет по ID", description = "Возвращает отчет по заданному ID")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        Report responseReport = adminService.getReportById(id).orElse(null);
        return ResponseEntity.of(Optional.ofNullable(responseReport));
    }

    @PatchMapping("/approveAuthRequest")
    @Operation(summary = "Одобрить запрос на авторизацию", description = "Одобряет запрос на авторизацию для заданного пользователя и возвращает обновленный список запросов на авторизацию")
    public ResponseEntity<List<User>> approveAuthRequest(@RequestBody User user) {
        Optional<List<User>> userList = adminService.approveAuthRequest(user);
        return ResponseEntity.ok(userList.orElseGet(ArrayList::new));
    }

    @DeleteMapping("/rejectAuthRequest")
    @Operation(summary = "Отклонить запрос на авторизацию", description = "Отклоняет запрос на авторизацию для заданного пользователя и возвращает обновленный список запросов на авторизацию")
    public ResponseEntity<List<User>> rejectAuthRequest(@RequestBody User user) {
        Optional<List<User>> userList = adminService.rejectAuthRequest(user);
        return ResponseEntity.ok(userList.orElseGet(ArrayList::new));
    }

    @PatchMapping("/changeUserRole")
    @Operation(summary = "Изменить роль пользователя", description = "Изменяет роль для заданного пользователя и возвращает обновленный список пользователей с их ролями")
    public ResponseEntity<List<UserDTO>> changeUserRole(@RequestBody UserDTO user) {
        Optional<List<User>> userList = adminService.changeUserRole(user.getUser(), user.getRole());
        List<UserDTO> userDTOsList = new ArrayList<>();
        userList.ifPresent(users -> {
            for (User curUser : users) {
                Authority authority = (Authority) curUser.getAuthorities().stream().findFirst().orElseThrow();
                Role curRole = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(curUser)
                        .role(curRole)
                        .build();
                userDTOsList.add(userDTO);
            }
        });
        return ResponseEntity.ok(userDTOsList);
    }

    @DeleteMapping("/deleteUser")
    @Operation(summary = "Удалить пользователя", description = "Удаляет заданного пользователя и возвращает обновленный список пользователей с их ролями")
    public ResponseEntity<List<UserDTO>> deleteUser(@RequestBody UserDTO user) {
        Optional<List<User>> userList = adminService.deleteUser(user.getUser());
        List<UserDTO> userDTOsList = new ArrayList<>();
        userList.ifPresent(users -> {
            for (User curUser : users) {
                Authority authority = (Authority) curUser.getAuthorities().stream().findFirst().orElseThrow();
                Role role = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(curUser)
                        .role(role)
                        .build();
                userDTOsList.add(userDTO);
            }
        });
        return ResponseEntity.ok(userDTOsList);
    }

    @PutMapping("/createReport")
    @Operation(summary = "Создать отчет", description = "Создает новый отчет и возвращает сообщение об успешном выполнении")
    public ResponseEntity<Map<String, Object>> createReport(@RequestBody Report report) {
        adminService.createReport(report);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", null);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/changeReport")
    @Operation(summary = "Изменить отчет", description = "Изменяет существующий отчет и возвращает сообщение об успешном выполнении")
    public ResponseEntity<Map<String, Object>> changeReport(@RequestBody Report report) {
        adminService.changeReport(report);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteReport/{id}")
    @Operation(summary = "Удалить отчет", description = "Удаляет отчет по заданному ID и возвращает сообщение об успешном выполнении")
    public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable Long id) {
        adminService.deleteReportById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getLogs")
    @Operation(summary = "Получить логи", description = "Возвращает список логов")
    public ResponseEntity<List<ReportRequest>> getLogs() {
        List<ReportRequest> logs = adminService.getReportRequests().orElseGet(ArrayList::new);
        return ResponseEntity.ok(logs);
    }

}