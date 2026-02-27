package com.fragparfum.Marjane.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String url;

    @Column(name = "public_id", nullable = false)
    private String publicId;
}
