package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/getAuthRequests")
    public ResponseEntity<Optional<List<User>>> getAuthRequests() {
        Optional<List<User>> userList = adminService.getAuthRequests();
        if(userList.isPresent()) {
            return ResponseEntity.ok(userList);
        } else {
            return ResponseEntity.ok(Optional.of(new ArrayList<>()));
        }
    }

    @PatchMapping("/approveAuthRequest")
    public ResponseEntity<Optional<List<User>>> approveAuthRequest(@RequestBody User user) {
        Optional<List<User>> userList = adminService.approveAuthRequest(user);
        if(userList.isPresent()) {
            return ResponseEntity.ok(userList);
        } else {
            return ResponseEntity.ok(Optional.of(new ArrayList<>()));
        }
    }

    @DeleteMapping("/rejectAuthRequest")
    public ResponseEntity<Optional<List<User>>> rejectAuthRequest(@RequestBody User user) {
        Optional<List<User>> userList = adminService.rejectAuthRequest(user);
        if(userList.isPresent()) {
            return ResponseEntity.ok(userList);
        } else {
            return ResponseEntity.ok(Optional.of(new ArrayList<>()));
        }
    }
}
