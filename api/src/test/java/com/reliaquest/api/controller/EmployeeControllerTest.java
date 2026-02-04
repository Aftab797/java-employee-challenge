package com.reliaquest.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private List<Employee> employees;

    @BeforeEach
    void setUp() {
        employee1 = Employee.builder()
                .id(UUID.randomUUID())
                .name("Coleman Feest")
                .salary(75000)
                .age(30)
                .title("Software Engineer")
                .email("coleman@company.com")
                .build();

        employee2 = Employee.builder()
                .id(UUID.randomUUID())
                .name("Mel Howell")
                .salary(85000)
                .age(35)
                .title("Senior Engineer")
                .email("howell@company.com")
                .build();

        employees = Arrays.asList(employee1, employee2);
    }

    @Test
    @DisplayName("GET /api/v1/employee - should return list of all employees")
    void getAllEmployees_shouldReturnListOfEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].employee_name").value("Coleman Feest"))
                .andExpect(jsonPath("$[1].employee_name").value("Mel Howell"));
    }

    @Test
    @DisplayName("GET /api/v1/employee - should return empty list when no employees exist")
    void getAllEmployees_shouldReturnEmptyList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/v1/employee/search/{searchString} - should return employees matching search string")
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() throws Exception {
        when(employeeService.getEmployeesByNameSearch("Coleman")).thenReturn(List.of(employee1));

        mockMvc.perform(get("/api/v1/employee/search/Coleman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employee_name").value("Coleman Feest"));
    }

    @Test
    @DisplayName("GET /api/v1/employee/{id} - should return employee by ID")
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        String id = employee1.getId().toString();
        when(employeeService.getEmployeeById(id)).thenReturn(employee1);

        mockMvc.perform(get("/api/v1/employee/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("Coleman Feest"))
                .andExpect(jsonPath("$.employee_salary").value(75000));
    }

    @Test
    @DisplayName("GET /api/v1/employee/highestSalary - should return highest salary among all employees")
    void getHighestSalaryOfEmployees_shouldReturnHighestSalary() throws Exception {
        when(employeeService.getHighestSalary()).thenReturn(85000);

        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("85000"));
    }

    @Test
    @DisplayName("GET /api/v1/employee/topTenHighestEarningEmployeeNames - should return top 10 earner names")
    void getTopTenHighestEarningEmployeeNames_shouldReturnNames() throws Exception {
        List<String> topEarners = Arrays.asList("Mel Howell", "Coleman Feest");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topEarners);

        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Mel Howell"))
                .andExpect(jsonPath("$[1]").value("Coleman Feest"));
    }

    @Test
    @DisplayName("POST /api/v1/employee - should create and return new employee")
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        EmployeeInput input = new EmployeeInput();
        input.setName("New Employee");
        input.setSalary(60000);
        input.setAge(25);
        input.setTitle("Junior Developer");

        Employee createdEmployee = Employee.builder()
                .id(UUID.randomUUID())
                .name("New Employee")
                .salary(60000)
                .age(25)
                .title("Junior Developer")
                .email("newe@company.com")
                .build();

        when(employeeService.createEmployee(any(EmployeeInput.class))).thenReturn(createdEmployee);

        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name").value("New Employee"))
                .andExpect(jsonPath("$.employee_salary").value(60000));
    }

    @Test
    @DisplayName("DELETE /api/v1/employee/{id} - should delete employee and return name")
    void deleteEmployeeById_shouldReturnEmployeeName() throws Exception {
        String id = employee1.getId().toString();
        when(employeeService.deleteEmployeeById(id)).thenReturn("Coleman Feest");

        mockMvc.perform(delete("/api/v1/employee/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Coleman Feest"));
    }

    @Test
    @DisplayName("DELETE /api/v1/employee/{id} - should return 404 when employee not found")
    void deleteEmployeeById_shouldReturnNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        when(employeeService.deleteEmployeeById(id)).thenReturn(null);

        mockMvc.perform(delete("/api/v1/employee/{id}", id)).andExpect(status().isNotFound());
    }
}
