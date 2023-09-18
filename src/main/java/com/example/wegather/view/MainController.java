package com.example.wegather.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
  @GetMapping("/")
  public String main() {
    return "redirect:/view";
  }

  @GetMapping("/view")
  public String index() {
    return "index";
  }

  @GetMapping("view/sign-in")
  public String signIn() {
    return "account/sign-in";
  }

  @GetMapping("view/sign-up")
  public String signUp() {
    return "account/sign-up";
  }
}
