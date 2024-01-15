package com.example.vms;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.assertNull;

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
