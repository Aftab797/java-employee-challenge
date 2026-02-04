package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
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
}
