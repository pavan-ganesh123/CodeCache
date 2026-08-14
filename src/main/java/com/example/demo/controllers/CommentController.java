package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class CommentController {
    @GetMapping("/comment")
    public String comment(@RequestParam String msg2) {
        return new String(msg2);
    }
    
}
