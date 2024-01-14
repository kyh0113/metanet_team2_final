package com.example.vms;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;
import com.example.vms.vacation.service.VacationService;

public class VacationServiceTest {

	@Mock
    private IUploadFileRepository uploadFileDaoMock;
	
	@Mock
	private IVacationRepository vacationDaoMock;
	
	@Mock
	private IEmployeeRepository employeeRepository;

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
	void testGetDeptNameByEmpId() {
		
		// empId 
        String empId = "I120001";
        String expectedDeptName = "it";

        // Mock the behavior of the employeeRepository
        when(employeeRepository.selectDeptNameByEmpId(empId)).thenReturn(expectedDeptName);

        // When
        String actualDeptName = vacationService.getDeptNameByEmpId(empId);

        // Then
        assertEquals(expectedDeptName, actualDeptName);

        // Verify that the selectDeptNameByEmpId method was called with the correct parameter
        verify(employeeRepository).selectDeptNameByEmpId(empId);
		
	}
	
	@Test
	void testGetScheduleListByOption() {
		
		// curPage
		
		
		// keyword 
		
		
		// option 
		
		
	}
	
	@Test 
	void testGetDeptRequestList() {
		
		// empId
		
		
		// state 
		
		
		// curPage 
		
		
		
	}
}
