package com.example.webbansach_backend.service.sach;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;

public interface SachService {
    public ResponseEntity<?> save(JsonNode bookJson);
    public ResponseEntity<?> update(JsonNode bookJson);
    public long layTongSoSach();
}
