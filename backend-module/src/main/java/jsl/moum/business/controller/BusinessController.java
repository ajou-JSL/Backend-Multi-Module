package jsl.moum.business.controller;

import jsl.moum.admin.service.AdminService;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.business.service.BusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/business")
@Slf4j
public class BusinessController {

    private final AdminService adminService;
    private final BusinessService businessService;

    @GetMapping("/practice-room/view/{id}")
    @ResponseBody
    public PracticeRoomDto getPracticeRoomDetails(@PathVariable(name = "id") int id) {
        return businessService.getPracticeRoomById(id);
    }

    @GetMapping("/performance-hall/view/{id}")
    @ResponseBody
    public PerformanceHallDto getPerformanceHallDetails(@PathVariable(name = "id") int id) {
        return businessService.getPerformanceHallById(id);
    }
}
