package com.gomad.h2_jpa.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BuildDTO {

    private Long id;
    private String version;
    private Long date;
}
