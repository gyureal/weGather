package com.example.wegather.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

  @GetMapping("/check-email-token")
  public String checkEmailToken(@RequestParam String email, @RequestParam String token, Model model) {
    model.addAttribute("email", email);
    model.addAttribute("token", token);
    return "account/check-email-token";
  }

  @GetMapping("/resend-confirm-email")
  public String resendConfirmEmailView() {
    return "account/resend-email";
  }
}
