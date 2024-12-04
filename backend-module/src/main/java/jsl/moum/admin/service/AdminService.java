package jsl.moum.admin.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.business.domain.PerformanceHall;
import jsl.moum.business.domain.PerformanceHallRepository;
import jsl.moum.business.domain.PracticeRoom;
import jsl.moum.business.domain.PracticeRoomRepository;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.report.domain.*;
import jsl.moum.report.dto.ArticleReportDto;
import jsl.moum.report.dto.MemberReportDto;
import jsl.moum.report.dto.TeamReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ChatroomRepository chatroomRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final PracticeRoomRepository practiceRoomRepository;
    private final PerformanceHallRepository performanceHallRepository;
    private final MemberReportRepository memberReportRepository;
    private final TeamReportRepository teamReportRepository;
    private final ArticleReportRepository articleReportRepository;
    private final ArticleRepository articleRepository;
    private final StorageService storageService;

    /**
     *
     * Main Dashboard total counts
     *
     */

    public Long getMemberCount() {
        return memberRepository.count();
    }

    public Long getTeamCount() {
        return teamRepository.count();
    }

    public Long getArticleCount(){
        return articleRepository.count();
    }

    public Long getChatroomCount() {
        return chatroomRepository.count();
    }

    public Long getPracticeRoomCount() {
        return practiceRoomRepository.count();
    }

    public Long getPerformanceHallCount() {
        return performanceHallRepository.count();
    }

    public Long getMemberReportCount(){
        return memberReportRepository.count();
    }

    public Long getTeamReportCount(){
        return teamReportRepository.count();
    }

    public Long getArticleReportCount(){
        return articleReportRepository.count();
    }

    public Long getBannedMemberCount(){
        return memberRepository.countByBanStatus(true);
    }

    /**
     *
     * Member Dashboard
     *
     */

    public List<MemberDto.Response> getMembers(){
        return memberRepository.findAll().stream().map(MemberDto.Response::new).toList();
    }

    public List<MemberReportDto.Response> getMemberReports(){
        return memberReportRepository.findAll().stream().map(MemberReportDto.Response::new).toList();
    }

    public Page<MemberDto.Info> getMembersPaged(PageRequest pageRequest){
        return memberRepository.findAll(pageRequest).map(MemberDto.Info::new);
    }

    public Page<MemberReportDto.Response> getMemberReportsPaged(PageRequest pageRequest){
        return memberReportRepository.findAll(pageRequest).map(MemberReportDto.Response::new);
    }

    public MemberDto.Info getMemberById(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다."));
        return new MemberDto.Info(member);
    }

    public MemberReportDto.Response getMemberReportById(int id){
        MemberReport report = memberReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 존재하지 않습니다."));
        return new MemberReportDto.Response(report);
    }

    public MemberDto.Info banMember(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다."));
        member.setBanStatus(true);
        member = memberRepository.save(member);
        return new MemberDto.Info(member);
    }

    public MemberDto.Info unbanMember(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다."));
        member.setBanStatus(false);
        member = memberRepository.save(member);
        return new MemberDto.Info(member);
    }

    public List<MemberDto.Response> getBannedMembers(){
        return memberRepository.findAllByBanStatus(true).stream().map(MemberDto.Response::new).toList();
    }

    public Page<MemberDto.Info> getBannedMembersPaged(PageRequest pageRequest){
        return memberRepository.findAllByBanStatusPaged(true, pageRequest).map(MemberDto.Info::new);
    }

    public MemberDto.Info setRoleAdmin(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
        member.setRole("ROLE_ADMIN");
        member = memberRepository.save(member);
        return new MemberDto.Info(member);
    }

    public MemberDto.Info setRoleUser(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
        member.setRole("ROLE_USER");
        member = memberRepository.save(member);
        return new MemberDto.Info(member);
    }

    /**
     *
     * Team Dashboard
     *
     */

    public List<TeamDto.Response> getTeams(){
        return teamRepository.findAll().stream().map(TeamDto.Response::new).toList();
    }

    public List<TeamReportDto.Response> getTeamReports(){
        return teamReportRepository.findAll().stream().map(TeamReportDto.Response::new).toList();
    }

    public Page<TeamDto.Response> getTeamsPaged(PageRequest pageRequest){
        return teamRepository.findAll(pageRequest).map(TeamDto.Response::new);
    }

    public Page<TeamReportDto.Response> getTeamReportsPaged(PageRequest pageRequest){
        return teamReportRepository.findAll(pageRequest).map(TeamReportDto.Response::new);
    }

    public TeamDto.Response getTeamById(int id){
        TeamEntity team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀 정보가 존재하지 않습니다."));
        return new TeamDto.Response(team);
    }

    public TeamReportDto.Response getTeamReportById(int id){
        TeamReport report = teamReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 존재하지 않습니다."));
        return new TeamReportDto.Response(report);
    }

    /**
     *
     * Article Dashboard
     *
     */

    public List<ArticleDto.Response> getArticles(){
        return articleRepository.findAll().stream().map(ArticleDto.Response::new).toList();
    }

    public List<ArticleReportDto.Response> getArticleReports(){
        return articleReportRepository.findAll().stream().map(ArticleReportDto.Response::new).toList();
    }

    public Page<ArticleDto.Response> getArticlesPaged(PageRequest pageRequest){
        return articleRepository.findAll(pageRequest).map(ArticleDto.Response::new);
    }

    public Page<ArticleReportDto.Response> getArticleReportsPaged(PageRequest pageRequest){
        return articleReportRepository.findAll(pageRequest).map(ArticleReportDto.Response::new);
    }

    public ArticleDto.Response getArticleById(int id){
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글 정보가 존재하지 않습니다."));
        return new ArticleDto.Response(article);
    }

    public ArticleReportDto.Response getArticleReportById(int id){
        ArticleReport report = articleReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 존재하지 않습니다."));
        return new ArticleReportDto.Response(report);
    }


    /**
     *
     * Chatroom Dashboard
     *
     */

    public List<ChatroomDto> getChatrooms(){
        return chatroomRepository.findAll().stream().map(ChatroomDto::new).toList();
    }

    public Page<ChatroomDto> getChatroomsPaged(PageRequest pageRequest){
        return chatroomRepository.findAll(pageRequest).map(ChatroomDto::new);
    }

    public ChatroomDto getChatroomById(int id){
        Chatroom chatroom = chatroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방 정보가 존재하지 않습니다."));
        return new ChatroomDto(chatroom);
    }

    /**
     *
     * Practice Room Dashboard
     *
     */

    public List<PracticeRoomDto> getPracticeRooms(){
        return practiceRoomRepository.findAll().stream().map(PracticeRoomDto::new).toList();
    }

    public PracticeRoomDto registerPracticeRoom(PracticeRoomDto.Register registerDto){
        if(!requiredFieldsCheckPracticeRoom(registerDto)){
            throw new CustomException(ErrorCode.REQUIRED_FIELDS_MISSING);
        }
        try {
            PracticeRoom room = registerDto.toEntity();
            room = practiceRoomRepository.save(room);
            return new PracticeRoomDto(room);
        } catch (Exception e){
            log.error("연습실 등록 중 오류 발생", e);
            throw new CustomException(ErrorCode.REGISTER_PRACTICE_ROOM_FAIL);
        }
    }

    public PracticeRoomDto savePracticeRoomImages(Integer id, List<MultipartFile> images) throws BadRequestException {
        log.info("adminService savePracticeRoomImages for room : {}", id);
        if (images.size() > 5) {
            log.error("업로드 이미지 개수 초과 (5개 초과)");
            throw new CustomException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        PracticeRoom room = practiceRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRACTICE_ROOM_NOT_FOUND));
        log.info("practiceRoom found : {}", room);

        try {
            List<String> imageUrls = uploadPracticeRoomImages(id, images);
            room.setImageUrls(imageUrls);
            room = practiceRoomRepository.save(room);
            return new PracticeRoomDto(room);
        } catch (Exception e){
            log.error("연습실 이미지 등록 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    public PracticeRoomDto updatePracticeRoom(int roomId, PracticeRoomDto.Update updateDto) {
        PracticeRoom room = practiceRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRACTICE_ROOM_NOT_FOUND));
        room.setName(updateDto.getName());
        room.setAddress(updateDto.getAddress());
        room.setOwner(updateDto.getOwner());
        room.setPhone(updateDto.getPhone());
        room.setEmail(updateDto.getEmail());
        room.setPrice(updateDto.getPrice());
        room.setCapacity(updateDto.getCapacity());
        room.setType(updateDto.getType());
        room.setStand(updateDto.getStand());
        room.setHasPiano(updateDto.isHasPiano());
        room.setHasAmp(updateDto.isHasAmp());
        room.setHasSpeaker(updateDto.isHasSpeaker());
        room.setHasMic(updateDto.isHasMic());
        room.setHasDrums(updateDto.isHasDrums());
        room.setDetails(updateDto.getDetails());

        room = practiceRoomRepository.save(room);
        return new PracticeRoomDto(room);
    }

    public boolean deletePracticeRoom(int roomId) {
        PracticeRoom room = practiceRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRACTICE_ROOM_NOT_FOUND));
        practiceRoomRepository.delete(room);
        return true;
    }

    private boolean requiredFieldsCheckPracticeRoom(PracticeRoomDto.Register registerDto){
        return registerDto.getName() != null
                && registerDto.getAddress() != null
                && registerDto.getOwner() != null
                && registerDto.getPhone() != null
                && registerDto.getEmail() != null
                && registerDto.getMapUrl() != null
                && registerDto.getLatitude() != null
                && registerDto.getLongitude() != null;
    }

    private List<String> uploadPracticeRoomImages(int targetId, List<MultipartFile> images) throws IOException {
        // "practiceRoom/{practiceRoomId}/{originalFileName}"
        List<String> imageUrls = new ArrayList<>();

        if(images != null && !images.isEmpty() && images.size() != 0){
            for(MultipartFile file : images){
                String originalFilename = file.getOriginalFilename();
                String key = "practiceRoom/" + targetId + "/" + originalFilename;
                String imageUrl = storageService.uploadImage(key, file);
                imageUrls.add(imageUrl);
            }
        }
        return imageUrls;
    }

    /**
     *
     * Performance Hall Dashboard
     *
     */

    public List<PerformanceHallDto> getPerformanceHalls(){
        return performanceHallRepository.findAll().stream().map(PerformanceHallDto::new).toList();
    }

    public PerformanceHallDto registerPerformanceHall(PerformanceHallDto.Register registerDto){
        if(!requiredFieldsCheckPerformanceHall(registerDto)){
            throw new CustomException(ErrorCode.REQUIRED_FIELDS_MISSING);
        }
        try {
            PerformanceHall hall = registerDto.toEntity();
            hall = performanceHallRepository.save(hall);
            return new PerformanceHallDto(hall);
        } catch (Exception e){
            log.error("공연장 등록 중 오류 발생", e);
            throw new CustomException(ErrorCode.REGISTER_PERFORMANCE_HALL_FAIL);
        }
    }

    public PerformanceHallDto savePerformanceHallImages(Integer id, List<MultipartFile> images) throws BadRequestException {
        log.info("adminService savePerformanceHallImages for hall : {}", id);
        if (images.size() > 5) {
            log.error("업로드 이미지 개수 초과 (5개 초과)");
            throw new CustomException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }

        PerformanceHall hall = performanceHallRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_HALL_NOT_FOUND));
        log.info("performanceHall found : {}", hall);

        try {
            List<String> imageUrls = uploadPerformanceHallImages(id, images);
            hall.setImageUrls(imageUrls);
            hall = performanceHallRepository.save(hall);
            return new PerformanceHallDto(hall);
        } catch (Exception e){
            log.error("공연장 이미지 등록 중 오류 발생", e);
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    public PerformanceHallDto updatePerformanceHall(int hallId, PerformanceHallDto.Update updateDto) {
        PerformanceHall hall = performanceHallRepository.findById(hallId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_HALL_NOT_FOUND));
        hall.setName(updateDto.getName());
        hall.setPrice(updateDto.getPrice());
        hall.setSize(updateDto.getSize());
        hall.setCapacity(updateDto.getCapacity());
        hall.setAddress(updateDto.getAddress());
        hall.setOwner(updateDto.getOwner());
        hall.setPhone(updateDto.getPhone());
        hall.setEmail(updateDto.getEmail());
        hall.setType(updateDto.getType());
        hall.setStand(updateDto.getStand());
        hall.setHasPiano(updateDto.isHasPiano());
        hall.setHasAmp(updateDto.isHasAmp());
        hall.setHasSpeaker(updateDto.isHasSpeaker());
        hall.setHasMic(updateDto.isHasMic());
        hall.setHasDrums(updateDto.isHasDrums());
        hall.setDetails(updateDto.getDetails());

        hall = performanceHallRepository.save(hall);
        return new PerformanceHallDto(hall);
    }

    public boolean deletePerformanceHall(int hallId) {
        PerformanceHall hall = performanceHallRepository.findById(hallId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_HALL_NOT_FOUND));
        performanceHallRepository.delete(hall);
        return true;
    }

    private boolean requiredFieldsCheckPerformanceHall(PerformanceHallDto.Register registerDto){
        return registerDto.getName() != null
                && registerDto.getAddress() != null
                && registerDto.getOwner() != null
                && registerDto.getPhone() != null
                && registerDto.getEmail() != null
                && registerDto.getMapUrl() != null
                && registerDto.getLatitude() != null
                && registerDto.getLongitude() != null;
    }

    private List<String> uploadPerformanceHallImages(int targetId, List<MultipartFile> images) throws IOException {
        // "performanceHall/{performanceHallId}/{originalFileName}"
        List<String> imageUrls = new ArrayList<>();

        if(images != null && !images.isEmpty() && images.size() != 0){
            for(MultipartFile file : images){
                String originalFilename = file.getOriginalFilename();
                String key = "practiceRoom/" + targetId + "/" + originalFilename;
                String imageUrl = storageService.uploadImage(key, file);
                imageUrls.add(imageUrl);
            }
        }
        return imageUrls;
    }
}
