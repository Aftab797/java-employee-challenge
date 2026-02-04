package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(properties = {"spring.retry.enabled=true"})
@DisplayName("EmployeeService Retry Integration Tests")
class EmployeeServiceRetryTest {

    @MockBean(name = "employeeRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private EmployeeService employeeService;

    @Test
    @DisplayName("getAllEmployees - should retry on 429 TooManyRequests and succeed on subsequent attempt")
    void getAllEmployees_shouldRetryOn429AndSucceed() {
        List<Employee> employees = Arrays.asList(Employee.builder()
                .id(UUID.randomUUID())
                .name("Test Employee")
                .salary(50000)
                .age(30)
                .title("Developer")
                .email("test@company.com")
                .build());

        Response<List<Employee>> successResponse = new Response<>();
        successResponse.setData(employees);

        HttpClientErrorException.TooManyRequests tooManyRequestsException =
                mock(HttpClientErrorException.TooManyRequests.class);

        // First call throws 429, second call succeeds
        when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenThrow(tooManyRequestsException)
                .thenReturn(ResponseEntity.ok(successResponse));

        List<Employee> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("Test Employee", result.get(0).getName());

        // Verify that the method was called twice (1 failure + 1 success)
        verify(restTemplate, times(2))
                .exchange(eq(""), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class));
    }
}
