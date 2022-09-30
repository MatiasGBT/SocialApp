package com.mgbt.socialapp_backend.controller;

import com.mgbt.socialapp_backend.model.entity.ReportReason;
import com.mgbt.socialapp_backend.model.service.ReportReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("api/report-reason/")
@PreAuthorize("isAuthenticated()")
public class ReportReasonController {

    @Autowired
    private ReportReasonService reportReasonService;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/get")
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
