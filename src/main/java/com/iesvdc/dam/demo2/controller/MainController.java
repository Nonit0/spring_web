package com.iesvdc.dam.demo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import com.iesvdc.dam.demo2.repositorios.RepoUsuario;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
// @RequestMapping("/admin")
public class MainController {
    @Autowired
    RepoUsuario repoUsuario;

    @GetMapping("")
    public String index(){
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/denegado")
    public String denegado() {
        return "denegado";
    }
    
      @GetMapping("/error")
      public String error(){
        return "error";
    }
}

