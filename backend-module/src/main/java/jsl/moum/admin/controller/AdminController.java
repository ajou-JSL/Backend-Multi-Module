package jsl.moum.admin.controller;

import jsl.moum.admin.dto.AdminLoginRequest;
import jsl.moum.admin.service.AdminService;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/login")
    public String adminLoginPage(Model model) {
        model.addAttribute("loginRequest", new AdminLoginRequest());
        return "adminLogin";
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        log.info("AdminController getDashboard");

        model.addAttribute("chatroomCount", adminService.getChatroomCount());
        model.addAttribute("memberCount", adminService.getMemberCount());
        model.addAttribute("teamCount", adminService.getTeamCount());

        return "adminDashboard";
    }

    @GetMapping("/logout")
    public String logout(){

        return "redirect:/admin/login";
    }

}
