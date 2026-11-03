package com.gomad.h2_jpa.controller;

import com.gomad.h2_jpa.payload.BuildDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class BuildController {

    @Value("${build.id:1}")
    private Long id;
    @Value("${build.version:v0}")
    private String version;
    @Value("${build.date:100000}")
    private Long date;

    @GetMapping("/build-info")
    public ResponseEntity<BuildDTO> getBuildInfo(){
        return new ResponseEntity<>(new BuildDTO(id, version, date), HttpStatus.OK);
    }
}
