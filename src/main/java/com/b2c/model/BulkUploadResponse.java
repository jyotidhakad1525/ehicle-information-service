package com.b2c.model;

import lombok.Data;

import java.util.List;

@Data
public class BulkUploadResponse {
    private int TotalCount;
    private int SuccessCount;
    private int FailedCount;
    private List<String> FailedRecords;
}
