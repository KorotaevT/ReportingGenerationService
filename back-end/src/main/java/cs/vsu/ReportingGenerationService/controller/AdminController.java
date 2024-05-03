package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dto.UserDTO;
import cs.vsu.ReportingGenerationService.enums.Role;
import cs.vsu.ReportingGenerationService.model.Authority;
import cs.vsu.ReportingGenerationService.model.User;
import cs.vsu.ReportingGenerationService.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
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

    @GetMapping("/getUsers")
    public ResponseEntity<List<UserDTO>> getUsers() {
        Optional<List<User>> userList = adminService.getUsers();
        List<UserDTO> userDTOsList = new ArrayList<>();

        if(userList.isPresent()) {
            for (User user : userList.get()) {
                Authority authority = (Authority) user.getAuthorities().stream().findFirst().orElseThrow();
                Role role = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(user)
                        .role(role)
                        .build();
                userDTOsList.add(userDTO);
            }
            return ResponseEntity.ok(userDTOsList);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
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

    @PatchMapping("/changeUserRole")
    public ResponseEntity<List<UserDTO>> changeUserRole(@RequestBody UserDTO user) {
        Optional<List<User>> userList = adminService.changeUserRole(user.getUser(), user.getRole());
        List<UserDTO> userDTOsList = new ArrayList<>();
        if(userList.isPresent()) {
            for (User curUser : userList.get()) {
                Authority authority = (Authority) curUser.getAuthorities().stream().findFirst().orElseThrow();
                Role curRole = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(curUser)
                        .role(curRole)
                        .build();
                userDTOsList.add(userDTO);
            }
            return ResponseEntity.ok(userDTOsList);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<List<UserDTO>> deleteUser(@RequestBody UserDTO user) {
        Optional<List<User>> userList = adminService.deleteUser(user.getUser());
        List<UserDTO> userDTOsList = new ArrayList<>();
        if(userList.isPresent()) {
            for (User curUser : userList.get()) {
                Authority authority = (Authority) curUser.getAuthorities().stream().findFirst().orElseThrow();
                Role role = authority.getRole();
                UserDTO userDTO = UserDTO.builder()
                        .user(curUser)
                        .role(role)
                        .build();
                userDTOsList.add(userDTO);
            }
            return ResponseEntity.ok(userDTOsList);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
