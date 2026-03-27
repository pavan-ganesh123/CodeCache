package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


@Controller
public class HomeController{
    @GetMapping("/leetcode")
    public String leetcodeHome(Model model){
        return "leetcode";
    }
    @GetMapping("/codechef")
    public String codechefHome(Model model) {
        return "codechef";
    }
    @GetMapping("/cses")
    public String csesHome(Model model) {
        return "cses";
    }
    @GetMapping("/codeforces")
    public String codeforceHome(Model model){
        return "codeforces";
    }
    
}
