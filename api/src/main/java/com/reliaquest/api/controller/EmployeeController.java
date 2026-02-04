package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import java.util.List;
import org.springframework.http.ResponseEntity;

public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {
    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return null;
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return null;
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return null;
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return null;
    }

    @Override
    public ResponseEntity<Employee> createEmployee(EmployeeInput employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }
}
