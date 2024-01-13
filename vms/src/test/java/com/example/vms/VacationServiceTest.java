package com.example.vms;

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

// 	@InjectMocks
// 	private IVacationService vacationService;

// 	@Mock
//     private IVacationRepository vacationRepository;
// 	@Mock
//     private ScheduleService scheduleService;
// 	@Mock
//     private EmployeeService employeeService;
	
// 	@Test
//     void testApprovalRequest() {
//         // Mock 객체 생성
//         //IVacationRepository vacationDaoMock = mock(IVacationRepository.class);
//         //ScheduleService scheduleServiceMock = mock(ScheduleService.class);
//         //EmployeeService employeeServiceMock = mock(EmployeeService.class);

//         // 가짜 응답 데이터 생성
//         Vacation fakeVacation = new Vacation();
//         fakeVacation.setRegId(1);
//         fakeVacation.setEmpId("employee123");
//         fakeVacation.setTypeId(1);
//         fakeVacation.setStartDate(LocalDate.of(2022, 1, 1));
//         fakeVacation.setEndDate(LocalDate.of(2022, 1, 5));

//         // Mock 객체에 대한 동작 설정
//         when(vacationRepository.selectRequestByRegId(1)).thenReturn(fakeVacation);
//         when(scheduleService.maxScheduleId()).thenReturn(10);
//         when(employeeService.selectEmployee("employee123")).thenReturn(new Employee());

//         // 테스트 대상 객체 생성 및 Mock 객체 주입
//         //IVacationService vacationService = new VacationService();
//         //vacationService.setVacationDao(vacationDaoMock);
//         //vacationService.setScheduleService(scheduleServiceMock);
//         //vacationService.setEmployeeService(employeeServiceMock);

//         // 테스트할 Vacation 객체 생성
//         Vacation vacation = new Vacation();
//         vacation.setRegId(1);

//         // 테스트 대상 메서드 호출
//         String result = vacationService.approvalRequest(vacation);

//         // 반환된 결과와 기대하는 결과가 같은지 검증
//         assertEquals("결재 완료", result);

//         // Mock 객체에 대한 메서드 호출 여부 검증
//         verify(vacation, times(1)).updateRequest(vacation);
//         verify(scheduleService, times(1)).insertSchedule(any(Schedule.class));
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

import com.example.vms.vacation.model.UploadFile;
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
}
