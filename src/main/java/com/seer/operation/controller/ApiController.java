package com.seer.operation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "api")
public class ApiController {
    @RequestMapping(value = "time")
    public String sysTime() {
        return new Date().toString();
    }
}
