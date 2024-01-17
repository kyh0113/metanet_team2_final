package com.example.vms;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.employee.service.EmployeeService;
import com.example.vms.manager.model.Employee;
import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.model.ScheduleEmpDeptType;
import com.example.vms.schedule.repository.IScheduleRepository;
import com.example.vms.schedule.service.ScheduleService;
import com.example.vms.vacation.model.UploadFile;
import com.example.vms.vacation.model.Vacation;
import com.example.vms.vacation.model.VacationEmployee;
import com.example.vms.vacation.repository.IUploadFileRepository;
import com.example.vms.vacation.repository.IVacationRepository;
import com.example.vms.vacation.service.VacationService;

@ExtendWith(MockitoExtension.class)
public class VacationServiceTest {
	
	@Mock
    private IUploadFileRepository uploadFileDaoMock;

    @Mock
    private IScheduleRepository scheduleDaoMock;  // 추가된 부분

	@Mock
	private IVacationRepository vacationDaoMock;
	
	@Mock
	private IEmployeeRepository employeeRepository;
	
	@Mock
    private ScheduleService scheduleServiceMock;  // ScheduleService mock 객체 생성 (필요에 따라 추가)
	
	@InjectMocks
	private ScheduleService scheduleService;

	@InjectMocks
	private VacationService vacationService;
	
 	@Mock
    private ScheduleService scheduleservice;
 	
 	@Mock
    private EmployeeService employeeService;
 	
    @Mock
	IScheduleRepository scheduleDao;

	@Mock
    private IScheduleRepository iScheduleRepository;
	

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
	public void testGetCountScheduleByOptionForEmpName() {
	    // Given
	    String keyword = "John Doe";
	    int option = 1;

	    when(scheduleDao.getCountScheduleByEmpName(keyword)).thenReturn(5);

	    // When
	    int actualRowNum = vacationService.getCountScheduleByOption(keyword, option);

	    // Then
	    assertEquals(5, actualRowNum);
	    verify(scheduleDao).getCountScheduleByEmpName(keyword);
	}

	@Test
	public void testGetCountScheduleByOptionForDeptName() {
	    // Given
	    String keyword = "IT";
	    int option = 2;

	    when(scheduleDao.getCountScheduleByDeptName(keyword)).thenReturn(8);

	    // When
	    int actualRowNum = vacationService.getCountScheduleByOption(keyword, option);

	    // Then
	    assertEquals(8, actualRowNum);
	    verify(scheduleDao).getCountScheduleByDeptName(keyword);
	}

	@Test
	public void testGetCountScheduleByOptionForPosition() {
	    // Given
	    String keyword = "Developer";
	    int option = 3;

	    when(scheduleDao.getCountScheduleByPosition(keyword)).thenReturn(3);

	    // When
	    int actualRowNum = vacationService.getCountScheduleByOption(keyword, option);

	    // Then
	    assertEquals(3, actualRowNum);
	    verify(scheduleDao).getCountScheduleByPosition(keyword);
	}

	@Test
	public void testGetCountScheduleByOptionForAll() {
	    // Given
	    int option = 4;

	    when(scheduleDao.getCountAllSchedule()).thenReturn(15);

	    // When
	    int actualRowNum = vacationService.getCountScheduleByOption(null, option);

	    // Then
	    assertEquals(15, actualRowNum);
	    verify(scheduleDao).getCountAllSchedule();
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
	
	@Test
    void testApprovalRequest_Success() {
        // 테스트 데이터
		Vacation vacation = new Vacation();
        vacation.setRegId(1);
        vacation.setEmpId("123");
        vacation.setStartDate(LocalDate.now());
        vacation.setEndDate(LocalDate.now().plusDays(1));
        vacation.setTypeId(1);
        vacation.setState("승인");

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
    public void testApprovalRequest() {
		// 테스트할 Vacation 객체 생성
        Vacation vacation = new Vacation();
        vacation.setRegId(123);
        vacation.setEmpId("employee123");
        vacation.setStartDate(LocalDate.now());
        vacation.setEndDate(LocalDate.now().plusDays(2)); // 예시로 2일 동안의 휴가
        vacation.setTypeId(1);
        vacation.setState("거절"); // "승인"이 아닌 상태로 설정

        // VacationDao의 mock 객체 설정
        when(vacationDaoMock.updateRequest(vacation)).thenReturn(1); // 휴가 승인 성공
        when(vacationDaoMock.selectRequestByRegId(vacation.getRegId())).thenReturn(vacation);

        // EmployeeService의 mock 객체 설정
        Employee employeeMock = mock(Employee.class);
        when(employeeMock.getDeptId()).thenReturn(1);
        when(employeeService.selectEmployee(anyString())).thenReturn(employeeMock);
        
        // Service 호출
        //VacationService vacationService = new VacationService(vacationDaoMock, scheduleservice, employeeService);
        String result = vacationService.approvalRequest(vacation);

        // "결재 완료"가 아니라 "결재 실패"가 반환되어야 함
        assertEquals("결재 완료", result);

        // ScheduleService의 어떠한 메서드도 호출되지 않아야 함
        verify(scheduleService, never()).maxScheduleId();
        verify(scheduleService, never()).insertSchedule(any(Schedule.class));
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
	
	@Test
	void testGetDeptNameByEmpId() {
		
		// 테스트하기 위해서 사용하는 변수들 
        String empId = "I120001";
        String expectedDeptName = "it";

        // employeeRepository 의 메서드 
        when(employeeRepository.selectDeptNameByEmpId(empId)).thenReturn(expectedDeptName);

        // 실제 deptName 
        String actualDeptName = vacationService.getDeptNameByEmpId(empId);

        // 비교 
        assertEquals(expectedDeptName, actualDeptName);

        // 증명
        verify(employeeRepository).selectDeptNameByEmpId(empId);
		
	}
	
	@Test
	void testGetScheduleListByEmpName() {
        String curPage = "1";
        String keyword = "J";
        int option = 1;
        int startNum = 1;
        int endNum = 10;

        ScheduleEmpDeptType schedule1 = new ScheduleEmpDeptType();
        schedule1.setCalendarId("1");
        schedule1.setDeptName("IT");
        schedule1.setEmpName("홍성철");
        schedule1.setPosition("Developer");
        schedule1.setTypeName("연차");
        schedule1.setStartDate(LocalDate.parse("2024-01-15"));
        schedule1.setEndDate(LocalDate.parse("2024-01-17"));
        
        ScheduleEmpDeptType schedule2 = new ScheduleEmpDeptType();
        schedule2.setCalendarId("2");
        schedule2.setDeptName("IT");
        schedule2.setEmpName("홍성철");
        schedule2.setPosition("HRManager");
        schedule2.setTypeName("연차");
        schedule2.setStartDate(LocalDate.parse("2024-01-18"));
        schedule2.setEndDate(LocalDate.parse("2024-01-20"));

        List<ScheduleEmpDeptType> expectedScheduleList = Arrays.asList(schedule1, schedule2);


        when(scheduleDao.getScheduleListByEmpName(startNum, endNum, keyword)).thenReturn(expectedScheduleList);

        List<ScheduleEmpDeptType> actualScheduleList = vacationService.getScheduleListByOption(curPage, keyword, option);

        assertEquals(expectedScheduleList, actualScheduleList);

        verify(scheduleDao).getScheduleListByEmpName(startNum, endNum, keyword);

	}
	
	

    @Test
    public void testGetScheduleListByOptionForDeptName() {
        // Given
        String curPage = "1";
        String keyword = "IT";
        int option = 2;
        int startNum = 1;
        int endNum = 10;

        ScheduleEmpDeptType schedule1 = new ScheduleEmpDeptType();
        schedule1.setCalendarId("1");
        schedule1.setDeptName("IT");
        schedule1.setEmpName("홍성철");
        schedule1.setPosition("Developer");
        schedule1.setTypeName("연차");
        schedule1.setStartDate(LocalDate.parse("2024-01-15"));
        schedule1.setEndDate(LocalDate.parse("2024-01-17"));
        
        ScheduleEmpDeptType schedule2 = new ScheduleEmpDeptType();
        schedule2.setCalendarId("2");
        schedule2.setDeptName("IT");
        schedule2.setEmpName("홍성철");
        schedule2.setPosition("HRManager");
        schedule2.setTypeName("연차");
        schedule2.setStartDate(LocalDate.parse("2024-01-18"));
        schedule2.setEndDate(LocalDate.parse("2024-01-20"));

        List<ScheduleEmpDeptType> expectedScheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleDao.getScheduleListByDeptName(startNum, endNum, keyword)).thenReturn(expectedScheduleList);

        List<ScheduleEmpDeptType> actualScheduleList = vacationService.getScheduleListByOption(curPage, keyword, option);

        assertEquals(expectedScheduleList, actualScheduleList);

        verify(scheduleDao).getScheduleListByDeptName(startNum, endNum, keyword);
    }

    @Test
    public void testGetScheduleListByOptionForPosition() {
        // Given
        String curPage = "1";
        String keyword = "Developer";
        int option = 3;
        int startNum = 1;
        int endNum = 10;

        ScheduleEmpDeptType schedule1 = new ScheduleEmpDeptType();
        schedule1.setCalendarId("1");
        schedule1.setDeptName("IT");
        schedule1.setEmpName("홍성철");
        schedule1.setPosition("Developer");
        schedule1.setTypeName("연차");
        schedule1.setStartDate(LocalDate.parse("2024-01-15"));
        schedule1.setEndDate(LocalDate.parse("2024-01-17"));
        
        ScheduleEmpDeptType schedule2 = new ScheduleEmpDeptType();
        schedule2.setCalendarId("2");
        schedule2.setDeptName("IT");
        schedule2.setEmpName("홍성철");
        schedule2.setPosition("HRManager");
        schedule2.setTypeName("연차");
        schedule2.setStartDate(LocalDate.parse("2024-01-18"));
        schedule2.setEndDate(LocalDate.parse("2024-01-20"));

        List<ScheduleEmpDeptType> expectedScheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleDao.getScheduleListByPosition(startNum, endNum, keyword)).thenReturn(expectedScheduleList);

        List<ScheduleEmpDeptType> actualScheduleList = vacationService.getScheduleListByOption(curPage, keyword, option);

        assertEquals(expectedScheduleList, actualScheduleList);

        verify(scheduleDao).getScheduleListByPosition(startNum, endNum, keyword);
    }

    @Test
    public void testGetScheduleListByOptionForAll() {
        // Given
        String curPage = "1";
        int option = 4;
        int startNum = 1;
        int endNum = 10;

        ScheduleEmpDeptType schedule1 = new ScheduleEmpDeptType();
        schedule1.setCalendarId("1");
        schedule1.setDeptName("IT");
        schedule1.setEmpName("홍성철");
        schedule1.setPosition("Developer");
        schedule1.setTypeName("연차");
        schedule1.setStartDate(LocalDate.parse("2024-01-15"));
        schedule1.setEndDate(LocalDate.parse("2024-01-17"));
        
        ScheduleEmpDeptType schedule2 = new ScheduleEmpDeptType();
        schedule2.setCalendarId("2");
        schedule2.setDeptName("IT");
        schedule2.setEmpName("홍성철");
        schedule2.setPosition("HRManager");
        schedule2.setTypeName("연차");
        schedule2.setStartDate(LocalDate.parse("2024-01-18"));
        schedule2.setEndDate(LocalDate.parse("2024-01-20"));

        List<ScheduleEmpDeptType> expectedScheduleList = Arrays.asList(schedule1, schedule2);

        when(scheduleDao.getAllScheduleList(startNum, endNum)).thenReturn(expectedScheduleList);

        List<ScheduleEmpDeptType> actualScheduleList = vacationService.getScheduleListByOption(curPage, null, option);

        assertEquals(expectedScheduleList, actualScheduleList);

        verify(scheduleDao).getAllScheduleList(startNum, endNum);
    }
	
    @Test
    public void testGetRequestList() {

    	String empId = "123";
        String state = "Pending";
        String curPage = "1";
        int curPageNum = Integer.parseInt(curPage);
        int startNum = curPageNum * 10 - 9;
        int endNum = curPageNum * 10;

        VacationEmployee vacation1 = new VacationEmployee(1, "Pending", LocalDate.parse("2024-01-15"),
                LocalDate.parse("2024-01-17"), LocalDate.parse("2024-01-01"), "John Doe");
        VacationEmployee vacation2 = new VacationEmployee(2, "Pending", LocalDate.parse("2024-01-18"),
                LocalDate.parse("2024-01-20"), LocalDate.parse("2024-01-02"), "Jane Smith");

        List<VacationEmployee> expectedRequestList = Arrays.asList(vacation1, vacation2);

        when(vacationDaoMock.selectRequestListByEmpId(empId, state, startNum, endNum)).thenReturn(expectedRequestList);

        List<VacationEmployee> actualRequestList = vacationService.getRequestList(empId, state, curPage);

        assertEquals(expectedRequestList, actualRequestList);

        verify(vacationDaoMock).selectRequestListByEmpId(empId, state, startNum, endNum);
    }

	
	@Test
    void testRequestVacation() throws IOException {
        // 테스트에 사용할 가짜 데이터
        Vacation vacation = new Vacation();
        vacation.setTypeId(1); // 예시로 사용하는 값

        MultipartFile[] files = new MultipartFile[2]; // 예시로 2개의 파일 배열 생성
        for (int i = 0; i < files.length; i++) {
            String content = "File Content " + i;
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            files[i] = new MockMultipartFile("file" + i, "file" + i + ".txt", "text/plain", contentBytes);
        }
        // 파일 데이터 및 기타 설정을 가짜로 생성

        // Mock 데이터에 대한 행동 설정
        when(vacationDaoMock.maxRegId()).thenReturn(42); // 예시로 사용하는 값
        when(uploadFileDaoMock.maxFileId()).thenReturn(10); // 예시로 사용하는 값

        // 테스트 메서드 호출
        vacationService.requestVacation(vacation, files);

        // 특정 메서드들이 정확히 호출되었는지 검증
        verify(vacationDaoMock, times(1)).maxRegId();
        verify(uploadFileDaoMock, times(2)).maxFileId();
        verify(vacationDaoMock, times(1)).requestVacation(any()); // 여기서 any()는 vacation 객체를 인자로 받는 호출을 의미

        // 파일 관련 메서드가 각 파일 수 만큼 호출되었는지 검증
        verify(uploadFileDaoMock, times(2)).insertUploadFile(any());

        // 만약 파일 데이터 저장에 대한 추가적인 검증이 필요하다면 파일 관련 메서드 호출 전후에 대해 추가 검증 수행
    }

    @Test
    void testGetVacationTypeName() {
        // 테스트에 사용할 가짜 데이터
        int typeId = 1; // 예시로 사용하는 값
        String expectedTypeName = "연차"; // 예시로 사용하는 값

        // Mock 데이터에 대한 행동 설정
        when(vacationDaoMock.selectTypeName(typeId)).thenReturn(expectedTypeName);

        // 테스트 메서드 호출
        String result = vacationService.getVacationTypeName(typeId);

        // 특정 메서드가 정확히 호출되었는지 검증
        verify(vacationDaoMock, times(1)).selectTypeName(typeId);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedTypeName, result);
    }
	
    @Test
    void testGetFileList() {
        // 테스트에 사용할 가짜 데이터
        int regId = 1; // 예시로 사용하는 값
        List<UploadFile> expectedFileList = Arrays.asList(
                new UploadFile("junitfile1", 1024, "text/plain"),
                new UploadFile("junitfile2", 2048, "text/plain")
        ); // 예시로 사용하는 값

        // Mock 데이터에 대한 행동 설정
        when(uploadFileDaoMock.selectFileListByRegId(regId)).thenReturn(expectedFileList);

        // 테스트 메서드 호출
        List<UploadFile> result = vacationService.getFileList(regId);

        // 특정 메서드가 정확히 호출되었는지 검증
        verify(uploadFileDaoMock, times(1)).selectFileListByRegId(regId);

        // 결과 값이 예상한 대로인지 검증
        assertEquals(expectedFileList, result);
    }
	
}

