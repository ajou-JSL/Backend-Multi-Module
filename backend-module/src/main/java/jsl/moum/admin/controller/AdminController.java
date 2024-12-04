package jsl.moum.admin.controller;

import jsl.moum.admin.dto.AdminLoginRequest;
import jsl.moum.admin.service.AdminService;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.business.service.BusinessService;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.report.dto.ArticleReportDto;
import jsl.moum.report.dto.MemberReportDto;
import jsl.moum.report.dto.TeamReportDto;
import jsl.moum.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;
    private final BusinessService businessService;

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

    /**
     *
     * Member Dashboard APIs
     *
     */

    @GetMapping("/member")
    public String getMemberDashboard(Model model){
        log.info("AdminController getMemberDashboard");

        model.addAttribute("memberCount", adminService.getMemberCount());
        model.addAttribute("memberReportCount", adminService.getMemberReportCount());
        model.addAttribute("bannedMemberCount", adminService.getBannedMemberCount());
        return "adminMemberDashboard";
    }

    @GetMapping("/member/list")
    public String getMemberList(Model model){
        log.info("AdminController getMemberList");

        model.addAttribute("memberCount", adminService.getMemberCount());
        model.addAttribute("members", adminService.getMembers());
        return "adminMemberList";
    }

    @GetMapping("/member/report/list")
    public String getMemberReportList(Model model){
        log.info("AdminController getMemberList");

        model.addAttribute("memberReportCount", adminService.getMemberReportCount());
        model.addAttribute("memberReports", adminService.getMemberReports());
        return "adminMemberReports";
    }

    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getMembers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MemberDto.Info> membersPage = adminService.getMembersPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("members", membersPage.getContent());
        response.put("currentPage", membersPage.getNumber() + 1);
        response.put("totalPages", membersPage.getTotalPages());
        response.put("totalMembers", membersPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/reports")
    public ResponseEntity<Map<String, Object>> getMemberReports(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MemberReportDto.Response> memberReportsPage = adminService.getMemberReportsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("memberReports", memberReportsPage.getContent());
        response.put("currentPage", memberReportsPage.getNumber() + 1);
        response.put("totalPages", memberReportsPage.getTotalPages());
        response.put("totalReports", memberReportsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/view/{id}")
    @ResponseBody
    public MemberDto.Info getMemberDetails(@PathVariable(name = "id") int id) {
        return adminService.getMemberById(id);
    }

    @GetMapping("/member/report/view/{id}")
    @ResponseBody
    public MemberReportDto.Response getMemberReportDetails(@PathVariable(name = "id") int id) {
        return adminService.getMemberReportById(id);
    }

    @DeleteMapping("/member/report/delete/{id}")
    public ResponseEntity<ResultResponse> deleteMemberReport(@PathVariable(name = "id") Integer id) {
        MemberReportDto.Response report = reportService.deleteMemberReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.DELETE_REPORT_MEMBER_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/member/banned/list")
    public String getBannedMemberList(Model model){
        log.info("AdminController getBannedMemberList");

        model.addAttribute("bannedMemberCount", adminService.getBannedMemberCount());
        model.addAttribute("bannedMembers", adminService.getBannedMembers());
        return "adminMemberBannedList";
    }

    @PostMapping("/member/{id}/ban")
    public ResponseEntity<ResultResponse> banMember(@PathVariable(name = "id") Integer id) {
        MemberDto.Info member = adminService.banMember(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.BAN_MEMBER_SUCCESS, member);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/member/{id}/unban")
    public ResponseEntity<ResultResponse> unbanMember(@PathVariable(name = "id") Integer id) {
        MemberDto.Info member = adminService.unbanMember(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.UNBAN_MEMBER_SUCCESS, member);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/member/banned")
    public ResponseEntity<Map<String, Object>> getBannedMembers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<MemberDto.Info> membersPage = adminService.getBannedMembersPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("members", membersPage.getContent());
        response.put("currentPage", membersPage.getNumber() + 1);
        response.put("totalPages", membersPage.getTotalPages());
        response.put("totalMembers", membersPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/member/{id}/role-admin")
    public ResponseEntity<ResultResponse> setRoleAdmin(@PathVariable(name = "id") Integer id) {
        MemberDto.Info member = adminService.setRoleAdmin(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.SET_ROLE_ADMIN_SUCCESS, member);
        return ResponseEntity.ok(resultResponse);
    }

    @PatchMapping("/member/{id}/role-user")
    public ResponseEntity<ResultResponse> setRoleUser(@PathVariable(name = "id") Integer id) {
        MemberDto.Info member = adminService.setRoleUser(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.SET_ROLE_USER_SUCCESS, member);
        return ResponseEntity.ok(resultResponse);
    }

    /**
     *
     * Team Dashboard APIs
     *
     */

    @GetMapping("/team")
    public String getTeamDashboard(Model model){
        log.info("AdminController getTeamDashboard");

        model.addAttribute("teamCount", adminService.getTeamCount());
        model.addAttribute("teamReportCount", adminService.getTeamReportCount());
        return "adminTeamDashboard";
    }

    @GetMapping("/team/list")
    public String getTeamList(Model model){
        log.info("AdminController getTeamList");

        model.addAttribute("teamCount", adminService.getTeamCount());
        model.addAttribute("teams", adminService.getTeams());
        return "adminTeamList";
    }

    @GetMapping("/team/report/list")
    public String getTeamReportList(Model model){
        log.info("AdminController getTeamReportList");

        model.addAttribute("teamReportCount", adminService.getTeamReportCount());
        model.addAttribute("teamReports", adminService.getTeamReports());
        return "adminTeamReports";
    }

    @GetMapping("/teams")
    public ResponseEntity<Map<String, Object>> getTeams(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<TeamDto.Response> teamsPage = adminService.getTeamsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("teams", teamsPage.getContent());
        response.put("currentPage", teamsPage.getNumber() + 1);
        response.put("totalPages", teamsPage.getTotalPages());
        response.put("totalTeams", teamsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/reports")
    public ResponseEntity<Map<String, Object>> getTeamReports(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<TeamReportDto.Response> teamReportsPage = adminService.getTeamReportsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("teamReports", teamReportsPage.getContent());
        response.put("currentPage", teamReportsPage.getNumber() + 1);
        response.put("totalPages", teamReportsPage.getTotalPages());
        response.put("totalReports", teamReportsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/team/view/{id}")
    @ResponseBody
    public TeamDto.Response getTeamDetails(@PathVariable(name = "id") int id) {
        return adminService.getTeamById(id);
    }

    @GetMapping("/team/report/view/{id}")
    @ResponseBody
    public TeamReportDto.Response getTeamReportDetails(@PathVariable(name = "id") int id) {
        return adminService.getTeamReportById(id);
    }

    @DeleteMapping("/team/report/delete/{id}")
    public ResponseEntity<ResultResponse> deleteTeamReport(@PathVariable(name = "id") Integer id) {
        TeamReportDto.Response report = reportService.deleteTeamReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.DELETE_REPORT_TEAM_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    /**
     *
     * Article Dashboard APIs
     *
     */

    @GetMapping("/article")
    public String getArticleDashboard(Model model){
        log.info("AdminController getArticleDashboard");

        model.addAttribute("articleCount", adminService.getArticleCount());
        model.addAttribute("articleReportCount", adminService.getArticleReportCount());
        return "adminArticleDashboard";
    }

    @GetMapping("/article/list")
    public String getArticleList(Model model){
        log.info("AdminController getArticleList");

        model.addAttribute("articleCount", adminService.getArticleCount());
        model.addAttribute("articles", adminService.getArticles());
        return "adminArticleList";
    }

    @GetMapping("/article/report/list")
    public String getArticleReportList(Model model){
        log.info("AdminController getArticleReportList");

        model.addAttribute("articleReportCount", adminService.getArticleReportCount());
        model.addAttribute("articleReports", adminService.getArticleReports());
        return "adminArticleReports";
    }

    @GetMapping("/articles")
    public ResponseEntity<Map<String, Object>> getArticles(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ArticleDto.Response> articlesPage = adminService.getArticlesPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("articles", articlesPage.getContent());
        response.put("currentPage", articlesPage.getNumber() + 1);
        response.put("totalPages", articlesPage.getTotalPages());
        response.put("totalArticles", articlesPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/article/reports")
    public ResponseEntity<Map<String, Object>> getArticleReports(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<ArticleReportDto.Response> articleReportsPage = adminService.getArticleReportsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("articleReports", articleReportsPage.getContent());
        response.put("currentPage", articleReportsPage.getNumber() + 1);
        response.put("totalPages", articleReportsPage.getTotalPages());
        response.put("totalReports", articleReportsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/article/view/{id}")
    @ResponseBody
    public ArticleDto.Response getArticleDetails(@PathVariable(name = "id") int id) {
        return adminService.getArticleById(id);
    }

    @GetMapping("/article/report/view/{id}")
    @ResponseBody
    public ArticleReportDto.Response getArticleReportDetails(@PathVariable(name = "id") int id) {
        return adminService.getArticleReportById(id);
    }

    @DeleteMapping("/article/report/delete/{id}")
    public ResponseEntity<ResultResponse> deleteArticleReport(@PathVariable(name = "id") Integer id) {
        ArticleReportDto.Response report = reportService.deleteArticleReport(id);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.DELETE_REPORT_ARTICLE_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    /**
     *
     * Chatroom Dashboard APIs
     *
     */

    @GetMapping("/chatroom")
    public String getChatroomDashboard(Model model){
        log.info("AdminController getChatroomDashboard");

        model.addAttribute("chatroomCount", adminService.getChatroomCount());
        model.addAttribute("chatrooms", adminService.getChatrooms());
        return "adminChatroomDashboard";
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<Map<String, Object>> getChatrooms(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
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

    /**
     *
     * Practice Room Dashboard APIs
     *
     */

    @GetMapping("/practice-room")
    public String getPracticeRoomDashboard(Model model){
        log.info("AdminController getPracticeRoomDashboard");

        model.addAttribute("practiceRoomCount", adminService.getPracticeRoomCount());
        model.addAttribute("practiceRooms", adminService.getPracticeRooms());

        return "adminPracticeRoomDashboard";
    }

    @GetMapping("/practice-room/view/{id}")
    @ResponseBody
    public PracticeRoomDto getPracticeRoomDetails(@PathVariable(name = "id") int id) {
        return businessService.getPracticeRoomById(id);
    }

    @GetMapping("/practice-rooms")
    public ResponseEntity<Map<String, Object>> getPracticeRooms(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PracticeRoomDto.Response> practiceRoomsPage = businessService.getPracticeRoomsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("practiceRooms", practiceRoomsPage.getContent());
        response.put("currentPage", practiceRoomsPage.getNumber() + 1);
        response.put("totalPages", practiceRoomsPage.getTotalPages());
        response.put("totalPracticeRooms", practiceRoomsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/practice-room/register/model-view")
    public String registerPracticeRoom(Model model) {
        return "adminPracticeRoomRegister";
    }

    @PostMapping("/practice-room/register")
    public ResponseEntity<ResultResponse> registerPracticeRoom(@RequestBody PracticeRoomDto.Register registerDto) {
        PracticeRoomDto practiceRoom = adminService.registerPracticeRoom(registerDto);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REGISTER_PRACTICE_ROOM_SUCCESS, practiceRoom);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping(value = "/practice-room/images", consumes = {"multipart/form-data"})
    public ResponseEntity<ResultResponse> uploadPracticeRoomImages(@RequestParam(name = "practiceRoomId") Integer practiceRoomId,
                                                                   @RequestPart(name = "images", required = true) List<MultipartFile> images) throws BadRequestException {
        PracticeRoomDto practiceRoom = adminService.savePracticeRoomImages(practiceRoomId, images);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.UPLOAD_PRACTICE_ROOM_IMAGE_SUCCESS, practiceRoom);
        return ResponseEntity.ok(resultResponse);
    }

    @PatchMapping("/practice-room/{id}")
    public ResponseEntity<ResultResponse> updatePracticeRoom(@PathVariable(name = "id") Integer id,
                                                             @RequestBody PracticeRoomDto.Update updateDto) {
        PracticeRoomDto practiceRoom = adminService.updatePracticeRoom(id, updateDto);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.UPDATE_PRACTICE_ROOM_SUCCESS, practiceRoom);
        return ResponseEntity.ok(resultResponse);
    }

    @DeleteMapping("/practice-room/{id}")
    public ResponseEntity<ResultResponse> deletePracticeRoom(@PathVariable(name = "id") Integer id) {
        boolean isDeleted = adminService.deletePracticeRoom(id);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.DELETE_PRACTICE_ROOM_SUCCESS, isDeleted);
        return ResponseEntity.ok(resultResponse);
    }

    /**
     *
     * Performance Hall Dashboard APIs
     *
     */

    @GetMapping("/performance-hall")
    public String getPerformanceHallDashboard(Model model){
        log.info("AdminController getPerformanceHallDashboard");

        model.addAttribute("performanceHallCount", adminService.getPerformanceHallCount());
        model.addAttribute("performanceHalls", adminService.getPerformanceHalls());

        return "adminPerformanceHallDashboard";
    }

    @GetMapping("/performance-hall/view/{id}")
    @ResponseBody
    public PerformanceHallDto getPerformanceHallDetails(@PathVariable(name = "id") int id) {
        return businessService.getPerformanceHallById(id);
    }

    @GetMapping("/performance-halls")
    public ResponseEntity<Map<String, Object>> getPerformanceHalls(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PerformanceHallDto.Response> performanceHallsPage = businessService.getPerformanceHallsPaged(pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("performanceHalls", performanceHallsPage.getContent());
        response.put("currentPage", performanceHallsPage.getNumber() + 1);
        response.put("totalPages", performanceHallsPage.getTotalPages());
        response.put("totalPerformanceHalls", performanceHallsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/performance-hall/register/model-view")
    public String registerPerformanceHall(Model model) {
        return "adminPerformanceHallRegister";
    }

    @PostMapping("/performance-hall/register")
    public ResponseEntity<ResultResponse> registerPerformanceHall(@RequestBody PerformanceHallDto.Register registerDto) {
        PerformanceHallDto performanceHall = adminService.registerPerformanceHall(registerDto);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REGISTER_PERFORMANCE_HALL_SUCCESS, performanceHall);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping(value = "/performance-hall/images", consumes = {"multipart/form-data"})
    public ResponseEntity<ResultResponse> uploadPerformanceHallImages(@RequestParam(name = "performanceHallId") Integer performanceHallId,
                                                                      @RequestPart(name = "images", required = true) List<MultipartFile> images) throws BadRequestException {
        PerformanceHallDto performanceHall = adminService.savePerformanceHallImages(performanceHallId, images);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.UPLOAD_PERFORMANCE_HALL_IMAGE_SUCCESS, performanceHall);
        return ResponseEntity.ok(resultResponse);
    }

    @PatchMapping("/performance-hall/{id}")
    public ResponseEntity<ResultResponse> updatePerformanceHall(@PathVariable(name = "id") Integer id,
                                                                @RequestBody PerformanceHallDto.Update updateDto) {
        PerformanceHallDto performanceHall = adminService.updatePerformanceHall(id, updateDto);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.UPDATE_PERFORMANCE_HALL_SUCCESS, performanceHall);
        return ResponseEntity.ok(resultResponse);
    }

    @DeleteMapping("/performance-hall/{id}")
    public ResponseEntity<ResultResponse> deletePerformanceHall(@PathVariable(name = "id") Integer id) {
        boolean isDeleted = adminService.deletePerformanceHall(id);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.DELETE_PERFORMANCE_HALL_SUCCESS, isDeleted);
        return ResponseEntity.ok(resultResponse);
    }

    @GetMapping("/logout")
    public String logout(){

        return "redirect:/admin/login";
    }


    /**
     *
     * Report related APIs
     *
     */


    @PostMapping("/report/member/{reportId}/reply")
    public ResponseEntity<ResultResponse> replyMemberReport(@PathVariable(name = "reportId") Integer reportId,
                                                            @RequestBody MemberReportDto.Reply reply) {

        MemberReportDto report = reportService.reportMemberReply(reportId, reply);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_MEMBER_REPLY_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/report/team/{reportId}/reply")
    public ResponseEntity<ResultResponse> replyTeamReport(@PathVariable(name = "reportId") Integer reportId,
                                                          @RequestBody TeamReportDto.Reply reply) {

        TeamReportDto report = reportService.reportTeamReply(reportId, reply);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_TEAM_REPLY_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }

    @PostMapping("/report/article/{reportId}/reply")
    public ResponseEntity<ResultResponse> replyArticleReport(@PathVariable(name = "reportId") Integer reportId,
                                                             @RequestBody ArticleReportDto.Reply reply) {

        ArticleReportDto report = reportService.reportArticleReply(reportId, reply);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REPORT_ARTICLE_REPLY_SUCCESS, report);
        return ResponseEntity.ok(resultResponse);
    }
}
