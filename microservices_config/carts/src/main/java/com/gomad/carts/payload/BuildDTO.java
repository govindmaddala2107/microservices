package com.gomad.carts.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BuildDTO {

    private Long id;
    private String version;
    private Long date;
    private String service;
}