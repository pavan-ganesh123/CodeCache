package com.example.demo.controllers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLController {
    @QueryMapping
    public String introduce(){
        return "This is GraphQl";
    }
}
