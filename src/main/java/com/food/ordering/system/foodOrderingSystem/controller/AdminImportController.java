package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.dto.BulkImportRequest;
import com.food.ordering.system.foodOrderingSystem.dto.BulkImportResult;
import com.food.ordering.system.foodOrderingSystem.service.BulkImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminImportController {

    @Autowired
    private BulkImportService bulkImportService;

    @PostMapping("/bulk-import")
    @PreAuthorize("hasRole('ADMIN')")
    public BulkImportResult bulkImport(@RequestBody BulkImportRequest request) {
        return bulkImportService.importBulk(request);
    }
}