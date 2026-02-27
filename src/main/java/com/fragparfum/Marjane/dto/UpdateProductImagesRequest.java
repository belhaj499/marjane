package com.fragparfum.Marjane.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateProductImagesRequest {
    private List<String> imageUrls = new ArrayList<>();
}
