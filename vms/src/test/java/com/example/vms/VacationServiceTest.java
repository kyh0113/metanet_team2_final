package com.example.vms;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

import com.example.vms.schedule.repository.IScheduleRepository;
import com.example.vms.schedule.service.ScheduleService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;
import com.example.vms.vacation.service.IVacationService;
import com.example.vms.vacation.service.VacationService;

public class VacationServiceTest {

	@Mock
	private IUploadFileRepository uploadFileDaoMock;

	@Mock
	private IVacationRepository vacationDaoMock;
	
	@Mock
	private IScheduleRepository scheduleDaoMock;
	
	@InjectMocks
	private ScheduleService scheduleService;

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

    @Test
    void testGetCountScheduleByOption() {
        String keyword = "John Doe";

        // Mock 데이터에 대한 행동 설정
        when(scheduleDaoMock.getCountScheduleByEmpName(keyword)).thenReturn(5);
        when(scheduleDaoMock.getCountScheduleByDeptName(keyword)).thenReturn(8);
        when(scheduleDaoMock.getCountScheduleByPosition(keyword)).thenReturn(3);
        when(scheduleDaoMock.getCountAllSchedule()).thenReturn(15);

        // 테스트 케이스 1: "사원명"
        assertEquals(5, scheduleService.getCountScheduleByEmpName(keyword));

        // 테스트 케이스 2: "부서"
        assertEquals(8, scheduleService.getCountScheduleByDeptName(keyword));

        // 테스트 케이스 3: "직위"
        assertEquals(3, scheduleService.getCountScheduleByPosition(keyword));

        // 테스트 케이스 4: 기본값 - "전체"
        assertEquals(15, scheduleService.getCountAllSchedule());
    }
    
    @Test
    void testGetDeptRequestList() {
        // 테스트 데이터를 직접 설정
        String empId = "employee123";
        String state = "pending";
        String curPage = "1";

        // VacationEmployee 객체 생성 (테스트 데이터)
        VacationEmployee vacation1 = new VacationEmployee();
        vacation1.setRegId(1);
        vacation1.setState(state);
        vacation1.setStartDate(LocalDate.of(2022, 1, 1));  // 예시 데이터, 필요에 따라 변경
        vacation1.setEndDate(LocalDate.of(2022, 1, 5));  // 예시 데이터, 필요에 따라 변경
        vacation1.setRegDate(LocalDate.now());  // 예시 데이터, 필요에 따라 변경
        vacation1.setEmpName("Employee1");  // 예시 데이터, 필요에 따라 변경

        VacationEmployee vacation2 = new VacationEmployee();
        vacation2.setRegId(2);
        vacation2.setState(state);
        vacation2.setStartDate(LocalDate.of(2022, 2, 1));  // 예시 데이터, 필요에 따라 변경
        vacation2.setEndDate(LocalDate.of(2022, 2, 5));  // 예시 데이터, 필요에 따라 변경
        vacation2.setRegDate(LocalDate.now());  // 예시 데이터, 필요에 따라 변경
        vacation2.setEmpName("Employee2");  // 예시 데이터, 필요에 따라 변경

        List<VacationEmployee> expectedList = Arrays.asList(vacation1, vacation2);

        // Mock 데이터에 대한 행동 설정
        when(vacationDaoMock.selectRequestListByDept(empId, state, 1, 10)).thenReturn(expectedList);

        // 테스트 메서드 호출
        List<VacationEmployee> resultList = vacationService.getDeptRequestList(empId, state, curPage);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedList, resultList);
    }
    
    @Test
    void testGetCountDeptRequestList() {
        // 테스트 데이터를 직접 설정
        String empId = "employee123";
        String state = "pending";

        // Mock 데이터에 대한 행동 설정
        when(vacationDaoMock.selectCountRequestListByDept(empId, state)).thenReturn(5); // 예시 데이터, 필요에 따라 변경

        // 테스트 메서드 호출
        int result = vacationService.getCountDeptRequestList(empId, state);

        // Mock 메서드가 정확히 호출되었는지 검증
        verify(vacationDaoMock, times(1)).selectCountRequestListByDept(empId, state);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(5, result); // 예상 데이터, 필요에 따라 변경
    }
}
