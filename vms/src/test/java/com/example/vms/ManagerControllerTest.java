package com.example.vms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;

import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.controller.ManagerController;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.service.IManagerService;
import com.example.vms.manager.service.ManagerService;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.vacation.service.VacationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@SpringBootTest
public class ManagerControllerTest {
	@InjectMocks
	private ManagerController managerController;

	@Mock
    private IManagerService managerService;
	@Mock
    private JwtTokenProvider tokenProvider;
	@Mock
    private EmployeeService employeeService;
	@Mock
    private VacationService vacationService;

    @BeforeEach
    public void setUp() {
//       
    }

    @Test
    public void testEmployeeListPage() {
        // HttpServletRequest와 Model을 Mock으로 생성
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model model = mock(Model.class);

        // 쿠키 정보 설정
        Cookie cookie = new Cookie("X-AUTH-TOKEN", "mockedToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // 토큰 유효성 검사 Mock 설정
        when(tokenProvider.validateToken("mockedToken")).thenReturn(true);
        when(tokenProvider.getEmpId("mockedToken")).thenReturn("mockedEmpId");

        // ManagerService에서 반환할 Employee Mock 설정
        Employee mockedEmployee = new Employee();
        when(managerService.selectEmployee("mockedEmpId")).thenReturn(mockedEmployee);

        // 테스트 수행
        String result = managerController.employeeListPage(request, model);

        // 결과 확인
        assertEquals("/manager/list", result);
    }

    @Test
    public void testEmployeeListPageRedirect() {
        // HttpServletRequest와 Model을 Mock으로 생성
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model model = mock(Model.class);

        // 쿠키 정보 설정
        Cookie cookie = new Cookie("X-AUTH-TOKEN", "invalidToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        // 토큰 유효성 검사 Mock 설정
        when(tokenProvider.validateToken("invalidToken")).thenReturn(false);

        // 테스트 수행
        String result = managerController.employeeListPage(request, model);

        // 결과 확인
        assertEquals("redirect:/employee/login", result);
    }
    
    @Test
    void testSearchEmployeeByEmpId() {
        // 가짜 응답 데이터 생성
        EmployeeResponseDTO fakeResponse = new EmployeeResponseDTO();
        fakeResponse.setEmpId("123");
        fakeResponse.setName("John Doe");

        // ManagerService가 메서드를 호출할 때 가짜 응답 데이터 반환하도록 설정
        when(managerService.searchEmployeeByEmpId("123")).thenReturn(fakeResponse);

        // 테스트할 empId 값
        String empId = "123";

        // 테스트 대상 메서드 호출
        EmployeeResponseDTO result = managerController.searchEmployeeByEmpId(empId);

        // 반환된 결과와 기대하는 결과가 같은지 검증
        assertEquals(fakeResponse, result);
    }
    
    @Test
    void testSelectEmployeeInfo() {
        // 가짜(모의) ManagerService 생성
        //ManagerService managerServiceMock = mock(ManagerService.class);

        // 가짜 응답 데이터 생성
        Employee fakeEmployee = new Employee();
        fakeEmployee.setEmpId("123");
        fakeEmployee.setName("John Doe");

        // ManagerService가 메서드를 호출할 때 가짜 응답 데이터 반환하도록 설정
        when(managerService.selectEmployee("123")).thenReturn(fakeEmployee);

        // 테스트 대상인 ManagerController 생성 및 mock ManagerService 주입
        //ManagerController managerController = new ManagerController();
        //managerController.setManagerService(managerServiceMock);

        // 테스트할 empId 값
        String empId = "123";

        // 테스트 대상 메서드 호출
        Employee result = managerController.selectEmployeeInfo(empId);

        // 반환된 결과와 기대하는 결과가 같은지 검증
        assertEquals(fakeEmployee, result);
    }
}
