package cs.vsu.ReportingGenerationService.controller;

import cs.vsu.ReportingGenerationService.dao.DynamicTableReader;
import cs.vsu.ReportingGenerationService.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("check");
    }
}
