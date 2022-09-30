package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Report;
import com.mgbt.socialapp_backend.model.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/report/")
@PreAuthorize("isAuthenticated()")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/post")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> createReport(@RequestBody Report report, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            reportService.save(report);
            response.put("message", messageSource.getMessage("reportController.reportPost", null, locale));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale));
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
