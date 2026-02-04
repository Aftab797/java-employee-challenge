package com.reliaquest.api.service;

import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.Response;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Retryable(
        retryFor = HttpClientErrorException.TooManyRequests.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 5000, multiplier = 2))
public class EmployeeService {

    private final RestTemplate restTemplate;

    public EmployeeService(@Qualifier("employeeRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() {
        log.debug("Fetching all employees from Mock API");
        ResponseEntity<Response<List<Employee>>> response =
                restTemplate.exchange("", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        if (response.getBody() != null && response.getBody().getData() != null) {
            List<Employee> employees = response.getBody().getData();
            log.info("Successfully fetched {} employees", employees.size());
            return employees;
        }
        log.info("Received empty response from Mock API");
        return Collections.emptyList();
    }

    public Employee getEmployeeById(String id) {
        log.debug("Fetching employee by id: {}", id);
        ResponseEntity<Response<Employee>> response =
                restTemplate.exchange("/{id}", HttpMethod.GET, null, new ParameterizedTypeReference<>() {}, id);

        Employee employee = response.getBody() != null ? response.getBody().getData() : null;
        if (employee != null) {
            log.info("Successfully fetched employee with id: {}", employee.getId());
        }
        return employee;
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.debug("Searching employees by name containing: {}", searchString);
        List<Employee> allEmployees = getAllEmployees();

        List<Employee> matchingEmployees = allEmployees.stream()
                .filter(employee -> employee.getName() != null
                        && employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();

        log.info("Found {} employees matching search string: {}", matchingEmployees.size(), searchString);
        return matchingEmployees;
    }

    public Integer getHighestSalary() {
        log.debug("Finding highest salary among all employees");
        List<Employee> allEmployees = getAllEmployees();

        Integer highestSalary = allEmployees.stream()
                .map(Employee::getSalary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);

        log.info("Highest salary found: {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.debug("Finding top 10 highest earning employee names");
        List<Employee> allEmployees = getAllEmployees();

        List<String> topEarners = allEmployees.stream()
                .filter(employee -> employee.getSalary() != null && employee.getName() != null)
                .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .toList();

        log.info("Found {} top earning employees", topEarners.size());
        return topEarners;
    }

    public Employee createEmployee(EmployeeInput employeeInput) {
        log.debug("Creating employee with name: {}", employeeInput.getName());
        HttpEntity<EmployeeInput> request = new HttpEntity<>(employeeInput);
        ResponseEntity<Response<Employee>> response =
                restTemplate.exchange("", HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

        Employee employee = response.getBody() != null ? response.getBody().getData() : null;
        if (employee != null) {
            log.info("Successfully created employee with id: {}", employee.getId());
        }
        return employee;
    }

    public String deleteEmployeeById(String id) {
        log.debug("Deleting employee with id: {}", id);

        Employee employee = getEmployeeById(id);

        String employeeName = employee.getName();

        DeleteEmployeeInput deleteInput = new DeleteEmployeeInput();
        deleteInput.setName(employeeName);

        HttpEntity<DeleteEmployeeInput> request = new HttpEntity<>(deleteInput);
        ResponseEntity<Response<Boolean>> response =
                restTemplate.exchange("", HttpMethod.DELETE, request, new ParameterizedTypeReference<>() {});

        boolean deleted = response.getBody() != null ? response.getBody().getData() : false;
        if (deleted) {
            log.info("Successfully deleted employee: {}", employeeName);
            return employeeName;
        }

        log.warn("Failed to delete employee: {}", employeeName);
        return null;
    }
}
