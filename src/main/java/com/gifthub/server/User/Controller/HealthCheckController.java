package com.gifthub.server.User.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("favicon.ico")
    public ResponseEntity<?> favicon() {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
