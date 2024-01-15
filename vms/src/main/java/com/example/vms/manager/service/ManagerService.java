package com.example.vms.manager.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vms.employee.repository.IEmployeeRepository;
import com.example.vms.manager.model.Department;
import com.example.vms.manager.model.Employee;
import com.example.vms.manager.model.EmployeeResponseDTO;
import com.example.vms.manager.model.EmployeeUpdateRequestDTO;
import com.example.vms.manager.repository.IManagerRepository;

@Service
public class ManagerService implements IManagerService {

    @Autowired
    private IManagerRepository managerDao;
    
    @Autowired
    private IEmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 부서별로 가장 큰 일련번호를 저장하는 맵
    private Map<Integer, Integer> departmentSequenceMap = new HashMap<>();

    @Override
    @Transactional
    public void create(Employee employee) {
        // 아이디 생성
        String id = generateEmployeeId(employee.getDeptId(), employee.getHireDate());

        // 아이디 및 비밀번호 설정
        employee.setEmpId(id);
        employee.setPassword(id);
        
        // 비밀번호 암호화
        String encodedPassword = encodePassword(employee.getPassword());
        employee.setPassword(encodedPassword);

        // DB에 저장
        managerDao.create(employee);
    }

    @Override
    public String generateEmployeeId(int deptId, LocalDate hireDate) {
        // 부서별로 가장 큰 일련번호 조회
        int maxId = maxEmployeeId(deptId); // 부서번호가 1인 가장 큰 일련번호 조회
        System.out.println("maxId: " + maxId + " 이건 처음엔 null일거임");

        // 부서별 일련번호 초기화 또는 가져오기
        int sequenceNumber = Math.max(maxId + 1, departmentSequenceMap.getOrDefault(deptId, 1));
        System.out.println("sequenceNumber: " + sequenceNumber + " 1않을까? null을 받았으니깐");

        // 아이디 생성
        String departmentInitial = managerDao.departmentName(deptId).substring(0, 1).toUpperCase();
        System.out.println("managerDao.departmentName(deptId): " + managerDao.departmentName(deptId));
        System.out.println(departmentInitial);
        String hireYearLastTwoDigits = String.format("%02d", hireDate.getYear() % 100);
        String employeeId = departmentInitial + hireYearLastTwoDigits + String.format("%04d", sequenceNumber);
        System.out.println(employeeId);
        // 부서별 일련번호 갱신
        departmentSequenceMap.put(deptId, sequenceNumber);
     // departmentSequenceMap 출력
        for (Map.Entry<Integer, Integer> entry : departmentSequenceMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        return employeeId;
    }

    private int maxEmployeeId(int deptId) {
        // 부서별로 가장 큰 일련번호를 DB에서 조회
        Integer maxId = managerDao.maxEmployeeId(deptId);
        return maxId != null ? maxId : 0; // null인 경우 0으로 처리하거나 다른 적절한 기본값 사용
    }

	@Override
	public String encodePassword(String password) {
		// 비밀번호 암호화
		return passwordEncoder.encode(password);
	}

	@Override
	public Employee selectEmployee(String empId) {
		return managerDao.selectEmployee(empId);
	}

	@Override
	public EmployeeResponseDTO[] searchEmployees(int start, int end, String empId) {
		if (empId==null || empId.equals("")) {
			return managerDao.searchEmployees(start, end);
		} else {
			System.out.println(empId);
			return managerDao.searchEmployeesWithEmpId(start, end, empId);
		}
	}

	@Override
	public int numberOfEmployees() {
		return managerDao.numberOfEmployees();
	}

	@Override
	public EmployeeResponseDTO searchEmployeeByEmpId(String empId) {
		EmployeeResponseDTO employee = managerDao.searchEmployeeByEmpId(empId);
		Set<String> roles = employeeRepository.getRolesByEmpId(employee.getEmpId());
		if (roles.contains("MANAGER")) {
			employee.setPosition("관리자");
		} else if (roles.contains("LEADER")) {
			employee.setPosition("팀장");
		} else if (roles.contains("EMPLOYEE")) {
			employee.setPosition("팀원");
		}
		return employee;
	}

	@Override
	@Transactional
	public void updateEmployee(EmployeeUpdateRequestDTO employee) {
		String position = employee.getPosition();
		String empId = employee.getEmpId(); 
		managerDao.deleteEmployeeRoles(empId);
		if (position.equals("관리자")) {
			managerDao.insertEmployeeRole("MANAGER", empId);
		} else if (position.equals("팀장")) {
			managerDao.insertEmployeeRole("LEADER", empId);
			managerDao.insertEmployeeRole("EMPLOYEE", empId);
		} else if (position.equals("팀원")) {
			managerDao.insertEmployeeRole("EMPLOYEE", empId);
		}
		managerDao.updateEmployee(employee);
	}

	@Override
	public Department[] searchDepartments() {
		return managerDao.searchDepartments();
	}

	@Override
	public int searchEmployeeRemains() {
		return 0;
	}

	@Override
	public List<Employee> getAllEmployees() {
		return managerDao.getAllEmployees();
	}

	@Override
	@Transactional
	public void updateRemains(String empId, int remains) {
		managerDao.updateRemains(empId, remains);
		
	}

	@Override
	public List<Employee> findEmployeesWithAtLeastOneVacation() {
		return managerDao.findEmployeesWithAtLeastOneVacation();
	}
}
