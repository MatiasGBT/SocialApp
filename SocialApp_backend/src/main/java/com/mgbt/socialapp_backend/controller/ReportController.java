package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.Post;
import com.mgbt.socialapp_backend.model.entity.Report;
import com.mgbt.socialapp_backend.model.service.ReportService;
import com.mgbt.socialapp_backend.utility_classes.JsonMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;
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

    @Operation(summary = "Gets all reports paginated")
    @ApiResponse(responseCode = "200", description = "Paginator object (paginator.content = array of reports)",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Page.class))) })
    @GetMapping("/get/list/{page}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getReports(@PathVariable Integer page,
                                        Locale locale) {
        try {
            Pageable pageable = PageRequest.of(page, 6);
            Page<Report> reports = reportService.toList(pageable);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @Operation(summary = "Delete a report")
    @ApiResponse(responseCode = "200", description = "Report deleted correctly",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = JsonMessage.class)) })
    @DeleteMapping("/delete/{idReport}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteReport(@PathVariable Long idReport, Locale locale) {
        Map<String, Object> response = new HashMap<>();
        try {
            Report report = reportService.findById(idReport);
            if (report != null) {
                reportService.delete(report);
            }
            response.put("message", messageSource.getMessage("reportController.deleteReport", null, locale));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", response.put("message", messageSource.getMessage("error.databaseOrFile", null, locale)));
            response.put("error", e.getMessage() + ": " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
