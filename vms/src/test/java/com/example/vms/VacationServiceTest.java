package com.example.vms;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.time.LocalDate;

// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.example.vms.employee.service.EmployeeService;
// import com.example.vms.manager.model.Employee;
// import com.example.vms.schedule.model.Schedule;
// import com.example.vms.schedule.service.ScheduleService;
// import com.example.vms.vacation.model.Vacation;
// import com.example.vms.vacation.repository.IVacationRepository;
// import com.example.vms.vacation.service.IVacationService;

// @SpringBootTest
// public class VacationServiceTest {

// 	

// 	
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.service.ScheduleService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;
import com.example.vms.vacation.service.VacationService;

public class VacationServiceTest {

	@Mock
    private IUploadFileRepository uploadFileDaoMock;
	
	@Mock
	private IVacationRepository vacationDaoMock;

	@InjectMocks
	private VacationService vacationService;
	
 	@Mock
    private ScheduleService scheduleservice;
 	
 	@Mock
    private EmployeeService employeeService;
 	
 	@Mock
    private ScheduleService scheduleserviceMock;
 	
 	@Mock
    private PlatformTransactionManager transactionManagerMock;
 	
 	@BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

	@Test
    void testMaxRegIdReturnsExpectedValue() {
        // Mock 데이터에 대한 행동 설정
        when(vacationDaoMock.maxRegId()).thenReturn(42);

        // 테스트 메서드 호출
        int result = vacationService.maxRegId();

        // 특정 메서드가 정확히 호출되었는지 검증
        verify(vacationDaoMock, times(1)).maxRegId();

        // 결과 값이 예상한 대로인지 검증
        assertEquals(42, result);
    }

	@Test
	void testDeleteVacation() {
		// 테스트 메서드 호출
		vacationService.deleteVacation(123); // 예시로 사용하는 regId

		// 특정 메서드가 정확히 호출되었는지 검증
		verify(vacationDaoMock, times(1)).deleteVacation(123);
	}
	
	@Test
    void testGetFile() {
        // 테스트에 사용할 fileId
        int fileId = 123;

        // Mock 데이터에 대한 행동 설정
        UploadFile expectedFile = new UploadFile();
        expectedFile.setFileId(fileId);
        when(uploadFileDaoMock.selectFile(fileId)).thenReturn(expectedFile);

        // 테스트 메서드 호출
        UploadFile result = vacationService.getFile(fileId);

        // 특정 메서드가 정확히 호출되었는지 검증
        verify(uploadFileDaoMock, times(1)).selectFile(fileId);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedFile, result);

        // 다른 fileId에 대한 테스트
        result = vacationService.getFile(456);

        // fileId가 일치하지 않는 경우에는 null을 반환해야 함
        assertNull(result);

    }
	
	@Test
    void testApprovalRequest_Success() {
        // 테스트 데이터
		Vacation vacation = new Vacation();
        vacation.setRegId(1);
        vacation.setEmpId("123");
        vacation.setStartDate(LocalDate.now());
        vacation.setEndDate(LocalDate.now().plusDays(1));
        vacation.setTypeId(1);

        when(vacationDaoMock.selectRequestByRegId(anyInt())).thenReturn(vacation);
        when(vacationDaoMock.updateRequest(any(Vacation.class))).thenReturn(1);
        when(scheduleservice.maxScheduleId()).thenReturn(100);

        Employee employeeMock = mock(Employee.class);
        when(employeeMock.getDeptId()).thenReturn(1);
        when(employeeService.selectEmployee(anyString())).thenReturn(employeeMock);

        // Act
        String result = vacationService.approvalRequest(vacation);

        // Assert
        assertEquals("결재 완료", result);

        // Verify 메서드 호출 확인
        verify(vacationDaoMock, times(1)).selectRequestByRegId(anyInt());
        verify(vacationDaoMock, times(1)).updateRequest(any(Vacation.class));
        verify(scheduleservice, times(1)).maxScheduleId();
        verify(employeeService, times(2)).selectEmployee(anyString());
        verify(employeeMock, times(1)).getDeptId();
    }

    @Test
    void testApprovalRequest_Failure() {
        // 테스트 데이터
        Vacation vacation = new Vacation();
        vacation.setRegId(1);
        vacation.setEmpId("123");
        vacation.setStartDate(LocalDate.now());
        vacation.setEndDate(LocalDate.now().plusDays(1));
        vacation.setTypeId(1);

        // Mock 객체 설정
        when(vacationDaoMock.updateRequest(vacation)).thenReturn(0);

        // 메서드 호출
        String result = vacationService.approvalRequest(vacation);

        // 결과 확인
        assertEquals("결재 실패", result);

        // Mock 객체 메서드 호출 여부 확인
        verify(vacationDaoMock, times(1)).updateRequest(vacation);
        verify(vacationDaoMock, never()).selectRequestByRegId(anyInt());
        verify(scheduleservice, never()).maxScheduleId();
        verify(employeeService, never()).selectEmployee(anyString());
    }

    @Test
    void testApprovalRequest_NoRequestInfo() {
        // 테스트 데이터
        Vacation vacation = new Vacation();
        vacation.setRegId(1);
        vacation.setEmpId("123");
        vacation.setStartDate(LocalDate.now());
        vacation.setEndDate(LocalDate.now().plusDays(1));
        vacation.setTypeId(1);

        // Mock 객체 설정
        when(vacationDaoMock.updateRequest(vacation)).thenReturn(1);
        when(vacationDaoMock.selectRequestByRegId(1)).thenReturn(null);

        // 메서드 호출
        String result = vacationService.approvalRequest(vacation);

        // 결과 확인
        assertEquals("신청서 정보 없음", result);

        // Mock 객체 메서드 호출 여부 확인
        verify(vacationDaoMock, times(1)).updateRequest(vacation);
        verify(vacationDaoMock, times(1)).selectRequestByRegId(1);
        verify(scheduleservice, never()).maxScheduleId();
        verify(employeeService, never()).selectEmployee(anyString());
    }
    
	@Test
    void testGetCountRequestList() {
        // 테스트 데이터 설정
        String empId = "testEmpId";
        String state = "testState";
        int expectedCount = 8;  // 예상되는 결과 값

        // Mock 객체에 대한 행동 설정
        when(vacationDaoMock.selectCountRequestListByEmpId(empId, state)).thenReturn(expectedCount);

        // 테스트 메서드 호출
        int result = vacationService.getCountRequestList(empId, state);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedCount, result);
    }
	
	@Test
    void testGetRequestDetail() {
        
        // 테스트 데이터 설정
        int regId = 123;  // 예상되는 결과 값

        // Mock 객체에 대한 행동 설정
        Vacation expectedVacation = new Vacation();  // 예상되는 결과 값
        when(vacationDaoMock.selectRequestByRegId(regId)).thenReturn(expectedVacation);

        // 테스트 메서드 호출
        Vacation result = vacationService.getRequestDetail(regId);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedVacation, result);
    }
}
