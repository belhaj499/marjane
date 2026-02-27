package com.fragparfum.Marjane.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloudinaryUploadResult {
    private String url;
    private String publicId;
}