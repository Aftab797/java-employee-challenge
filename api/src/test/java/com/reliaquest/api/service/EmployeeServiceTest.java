package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
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

        employee3 = Employee.builder()
                .id(UUID.randomUUID())
                .name("John Coleman")
                .salary(95000)
                .age(40)
                .title("Tech Lead")
                .email("john@company.com")
                .build();

        employees = Arrays.asList(employee1, employee2, employee3);
    }

    @Test
    @DisplayName("getAllEmployees - should return list of employees from API")
    void getAllEmployees_shouldReturnEmployees() {
        Response<List<Employee>> response = new Response<>();
        response.setData(employees);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(3, result.size());
        assertEquals("Coleman Feest", result.get(0).getName());
        verify(restTemplate, times(1))
                .exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("getAllEmployees - should return empty list when API returns null data")
    void getAllEmployees_shouldReturnEmptyListWhenNullData() {
        Response<List<Employee>> response = new Response<>();
        response.setData(null);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        List<Employee> result = employeeService.getAllEmployees();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getEmployeeById - should return employee when found")
    void getEmployeeById_shouldReturnEmployee() {
        String id = employee1.getId().toString();
        Response<Employee> response = new Response<>();
        response.setData(employee1);

        when(restTemplate.exchange(
                        eq("/{id}"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), eq(id)))
                .thenReturn(ResponseEntity.ok(response));

        Employee result = employeeService.getEmployeeById(id);

        assertNotNull(result);
        assertEquals("Coleman Feest", result.getName());
        assertEquals(75000, result.getSalary());
    }

    @Test
    @DisplayName("getEmployeesByNameSearch - should return employees matching search string (case-insensitive)")
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() {
        Response<List<Employee>> response = new Response<>();
        response.setData(employees);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        List<Employee> result = employeeService.getEmployeesByNameSearch("coleman");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getName().toLowerCase().contains("coleman")));
    }

    @Test
    @DisplayName("getEmployeesByNameSearch - should return empty list when no matches")
    void getEmployeesByNameSearch_shouldReturnEmptyListWhenNoMatches() {
        Response<List<Employee>> response = new Response<>();
        response.setData(employees);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        List<Employee> result = employeeService.getEmployeesByNameSearch("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getHighestSalary - should return highest salary among all employees")
    void getHighestSalary_shouldReturnHighestSalary() {
        Response<List<Employee>> response = new Response<>();
        response.setData(employees);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        Integer result = employeeService.getHighestSalary();

        assertEquals(95000, result);
    }

    @Test
    @DisplayName("getHighestSalary - should return 0 when no employees exist")
    void getHighestSalary_shouldReturnZeroWhenNoEmployees() {
        Response<List<Employee>> response = new Response<>();
        response.setData(Collections.emptyList());

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        Integer result = employeeService.getHighestSalary();

        assertEquals(0, result);
    }

    @Test
    @DisplayName("getTopTenHighestEarningEmployeeNames - should return names sorted by salary descending")
    void getTopTenHighestEarningEmployeeNames_shouldReturnSortedNames() {
        Response<List<Employee>> response = new Response<>();
        response.setData(employees);

        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertEquals(3, result.size());
        assertEquals("John Coleman", result.get(0));
        assertEquals("Mel Howell", result.get(1));
        assertEquals("Coleman Feest", result.get(2));
    }

    @Test
    @DisplayName("createEmployee - should create and return new employee")
    void createEmployee_shouldReturnCreatedEmployee() {
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

        Response<Employee> response = new Response<>();
        response.setData(createdEmployee);

        when(restTemplate.exchange(
                        eq(""), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));

        Employee result = employeeService.createEmployee(input);

        assertNotNull(result);
        assertEquals("New Employee", result.getName());
        assertEquals(60000, result.getSalary());
    }

    @Test
    @DisplayName("deleteEmployeeById - should delete employee and return name")
    void deleteEmployeeById_shouldReturnEmployeeName() {
        String id = employee1.getId().toString();

        Response<Employee> getResponse = new Response<>();
        getResponse.setData(employee1);

        Response<Boolean> deleteResponse = new Response<>();
        deleteResponse.setData(true);

        when(restTemplate.exchange(
                        eq("/{id}"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), eq(id)))
                .thenReturn(ResponseEntity.ok(getResponse));

        when(restTemplate.exchange(
                        eq(""), eq(HttpMethod.DELETE), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(deleteResponse));

        String result = employeeService.deleteEmployeeById(id);

        assertEquals("Coleman Feest", result);
    }

    @Test
    @DisplayName("deleteEmployeeById - should return null when delete fails")
    void deleteEmployeeById_shouldReturnNullWhenDeleteFails() {
        String id = employee1.getId().toString();

        Response<Employee> getResponse = new Response<>();
        getResponse.setData(employee1);

        Response<Boolean> deleteResponse = new Response<>();
        deleteResponse.setData(false);

        when(restTemplate.exchange(
                        eq("/{id}"), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class), eq(id)))
                .thenReturn(ResponseEntity.ok(getResponse));

        when(restTemplate.exchange(
                        eq(""), eq(HttpMethod.DELETE), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(deleteResponse));

        String result = employeeService.deleteEmployeeById(id);

        assertNull(result);
    }
}
