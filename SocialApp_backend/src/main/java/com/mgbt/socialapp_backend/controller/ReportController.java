package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Report;
import com.mgbt.socialapp_backend.model.service.ReportService;
import com.mgbt.socialapp_backend.utility_classes.JsonMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/reports/")
@PreAuthorize("isAuthenticated()")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Creates a report with the request body")
    @ApiResponse(responseCode = "201", description = "Report created correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
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
