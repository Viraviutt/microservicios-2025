package com.example.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostNameController {

    private Environment env;

    @Autowired
    public HostNameController(Environment env){
        this.env = env;
    }

    @GetMapping(path = "/hostname")
    public ResponseEntity<String> hostname(){
        String hostname = env.getProperty("HOSTNAME"); //(container ID)
        return ResponseEntity.ok().body(hostname);
    }
}