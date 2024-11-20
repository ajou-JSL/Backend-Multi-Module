package jsl.moum.admin.controller;

import jsl.moum.admin.dto.AdminLoginRequest;
import jsl.moum.admin.service.AdminService;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        model.addAttribute("practiceRoomCount", adminService.getPracticeRoomCount());
        model.addAttribute("performanceHallCount", adminService.getPerformanceHallCount());

        return "adminDashboard";
    }

    @GetMapping("/chatroom")
    public String getChatroomDashboard(Model model){
        log.info("AdminController getChatroomDashboard");

        model.addAttribute("chatroomCount", adminService.getChatroomCount());
        model.addAttribute("chatrooms", adminService.getChatrooms());
        return "adminChatroom";
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<Map<String, Object>> getChatrooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ChatroomDto> chatroomsPage = adminService.getChatroomsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("chatrooms", chatroomsPage.getContent());
        response.put("currentPage", chatroomsPage.getNumber() + 1);
        response.put("totalPages", chatroomsPage.getTotalPages());
        response.put("totalChatrooms", chatroomsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/chatroom/view/{id}")
    @ResponseBody
    public ChatroomDto getChatroomDetails(@PathVariable int id) {
        return adminService.getChatroomById(id);
    }

    @GetMapping("/member")
    public String getMemberDashboard(Model model){
        log.info("AdminController getMemberDashboard");

        model.addAttribute("memberCount", adminService.getMemberCount());
        model.addAttribute("members", adminService.getMembers());
        return "adminMember";
    }

    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getMembers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MemberEntity> membersPage = adminService.getMembersPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("members", membersPage.getContent());
        response.put("currentPage", membersPage.getNumber() + 1);
        response.put("totalPages", membersPage.getTotalPages());
        response.put("totalMembers", membersPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/view/{id}")
    @ResponseBody
    public MemberDto.Response getMemberDetails(@PathVariable int id) {
        return adminService.getMemberById(id);
    }

    @GetMapping("/team")
    public String getTeamDashboard(Model model){
        log.info("AdminController getTeamDashboard");

        model.addAttribute("teamCount", adminService.getTeamCount());
        model.addAttribute("teams", adminService.getTeams());
        return "adminTeam";
    }

    @GetMapping("/team/view/{id}")
    @ResponseBody
    public TeamDto.Response getTeamDetails(@PathVariable int id) {
        return adminService.getTeamById(id);
    }

    @GetMapping("/performance-hall")
    public String getPerformanceHallDashboard(Model model){
        log.info("AdminController getPerformanceHallDashboard");

        model.addAttribute("performanceHallCount", adminService.getPerformanceHallCount());
        model.addAttribute("performanceHalls", adminService.getPerformanceHalls());

        return "adminPerformanceHall";
    }

    @GetMapping("/performance-hall/view/{id}")
    @ResponseBody
    public PerformanceHallDto getPerformanceHallDetails(@PathVariable int id) {
        return adminService.getPerformanceHallById(id);
    }

    @GetMapping("/practice-room")
    public String getPracticeRoomDashboard(Model model){
        log.info("AdminController getPracticeRoomDashboard");

        model.addAttribute("practiceRoomCount", adminService.getPracticeRoomCount());
        model.addAttribute("practiceRooms", adminService.getPracticeRooms());

        return "adminPracticeRoom";
    }

    @GetMapping("/practice-room/view/{id}")
    @ResponseBody
    public PracticeRoomDto getPracticeRoomDetails(@PathVariable int id) {
        return adminService.getPracticeRoomById(id);
    }

    @GetMapping("/logout")
    public String logout(){

        return "redirect:/admin/login";
    }

}
