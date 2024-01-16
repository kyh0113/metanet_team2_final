package com.example.vms.schedule.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.vms.employee.service.EmployeeService;
import com.example.vms.jwt.JwtTokenProvider;
import com.example.vms.manager.model.Employee;
import com.example.vms.schedule.model.Schedule;
import com.example.vms.schedule.service.IScheduleService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class ScheduleController {

	@Autowired
	private final IScheduleService iScheduleService;
	
	@Autowired
    private JwtTokenProvider tokenProvider;
	
	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/look")
	public String calendar(Model model) {

		return "calendar/calendar";
	}

	@GetMapping("/calendar-admin")
	@ResponseBody
	public List<Map<String, Object>> monthPlan(HttpServletRequest request, HttpServletResponse response) {
		
		Cookie[] cookies = request.getCookies();
        System.out.println(cookies.toString());
        String token = "";
        for(Cookie cookie : cookies) {
           if(cookie.getName().equals("X-AUTH-TOKEN")) {
              token = cookie.getValue();
           }
        }
        
        String empId = tokenProvider.getEmpId(token);
        System.out.println("로그인한 사원:"+empId);
 
		// List<Schedule> schedules = iScheduleService.getSchedulebydeptId(deptid);
		List<Schedule> schedules = iScheduleService.getSchedulebydeptId
				(employeeService.selectEmployee(empId).getDeptId());
		// List<Schedule> schedules =
		// iVacationService.getApprDeptRequestList(empId,"승인");

		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();

		HashMap<String, Object> hash = new HashMap<>();

		for (int i = 0; i < schedules.size(); i++) {
			hash.put("title", schedules.get(i).getTitle());
			hash.put("start", schedules.get(i).getStart_date());
			hash.put("end", schedules.get(i).getEnd_date());
			hash.put("allDay", true);

			int color = schedules.get(i).getType_id();
			System.out.println(hash.get("start"));
			switch (color) {
			case 1:
				hash.put("color", "#0000FF");
				break;
			case 2:
				hash.put("color", "#00FF00");
				break;
			case 3:
				hash.put("color", "orange");
				break;
			default:
				hash.put("color", "#000000");
			}

			jsonObj = new JSONObject(hash);
			jsonArr.add(jsonObj);
		}

		return jsonArr;
	}
	
	Workbook wb = new XSSFWorkbook();
	public void setDateCellStyle(Cell cell, LocalDate date) {
	    CellStyle dateCellStyle = wb.createCellStyle();
	    CreationHelper createHelper = wb.getCreationHelper();
	    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

	    cell.setCellValue(date);
	    cell.setCellStyle(dateCellStyle);
	}

	@ResponseBody
	@GetMapping("/download")
	public void excelDownload(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    
		String dateTime=new Date().toString().replaceAll(":", "_");

		//wsSheet = createSheet(sheetName, xssfWorkbook) as sheetName+dateTime;
	    Sheet sheet = wb.createSheet(dateTime);
	    Row row;
	    Cell cell;
	    int rowNum = 0;

	    CellStyle cellStyle = wb.createCellStyle();
	    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
	    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    Cookie[] cookies = request.getCookies();

		String token = "";
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("X-AUTH-TOKEN")) {
				token = cookie.getValue();
			}
		}
		
		String empId = tokenProvider.getEmpId(token);

        Employee employee = employeeService.selectEmployee(empId);

	    List<Schedule> schedules = iScheduleService.getSchedulebydeptId(employee.getDeptId());

	    // Header
	    row = sheet.createRow(rowNum++);
	    for (int i = 0; i < 4; i++) {
	        cell = row.createCell(i);
	        cell.setCellStyle(cellStyle);
	        if (i == 0) {
	            cell.setCellValue("사번");
	        } else if (i == 1) {
	            cell.setCellValue("제목");
	        } else if (i == 2) {
	            cell.setCellValue("시작날짜");
	        } else if (i == 3) {
	            cell.setCellValue("종료날짜");
	        }
	    }

	    // Body
	    for (Schedule schedule : schedules) {
	        row = sheet.createRow(rowNum++);
	        for (int i = 0; i < 4; i++) {
	            cell = row.createCell(i);
	            if (i == 0) {
	                cell.setCellValue(schedule.getEmp_id());
	            } else if (i == 1) {
	                cell.setCellValue(schedule.getTitle());
	            } else if (i == 2) {
	                setDateCellStyle(cell, schedule.getStart_date());
	            } else if (i == 3) {
	                setDateCellStyle(cell, schedule.getEnd_date().minusDays(1)); 
	            }
	        }
	    }

	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment;filename=calendar_" + dateTime + ".xlsx");
	    //response.setHeader("Content-Disposition", "attachment;filename=calendar.xlsx");

	    //wb.write(response.getOutputStream());
	    //wb.close();
	    ServletOutputStream out = response.getOutputStream();
	    wb.write(out);
	    out.flush();
	    
	}


}