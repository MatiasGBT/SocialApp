package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.ReportReason;
import com.mgbt.socialapp_backend.model.service.ReportReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/report-reasons/")
@PreAuthorize("isAuthenticated()")
public class ReportReasonController {

    @Autowired
    private ReportReasonService reportReasonService;

    @Autowired
    MessageSource messageSource;

    @Operation(summary = "Gets a list of report reasons")
    @ApiResponse(responseCode = "200", description = "Array of report reasons",
            content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReportReason.class))) })
    @GetMapping("/get/list")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<?> getReasons(Locale locale) {
        try {
            List<ReportReason> reportReasons = reportReasonService.toList();
            return new ResponseEntity<>(reportReasons, HttpStatus.OK);
        } catch (DataAccessException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", messageSource.getMessage("error.database", null, locale));
            response.put("error", e.getMessage() + ": " + e.getMostSpecificCause().getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
