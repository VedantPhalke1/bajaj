package com.example.demo;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SqlSolutionService {
    private final RestTemplate restTemplate;
    
    public SqlSolutionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void executeSolutionFlow() {
        try {
            System.out.println("Starting solution flow...");
            
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse != null && webhookResponse.getWebhook() != null) {
                System.out.println("Webhook generated successfully");
                
                // Step 2: Create the SQL query based on Question 2
                String sqlQuery = createSqlQuery();
                System.out.println("SQL Query created: " + sqlQuery);
                
                // Step 3: Submit the solution
                submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            } else {
                System.out.println("Failed to generate webhook");
            }
        } catch (Exception e) {
            System.err.println("Error in solution flow: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private WebhookResponse generateWebhook() {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        
        WebhookRequest request = new WebhookRequest(
            "Vedant Phalke", 
            "22BCE2210", 
            "vedant@example.com"
        );
        
        try {
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                url, request, WebhookResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private String createSqlQuery() {
        // SQL query for Question 2: Count of younger employees in the same department
        return "SELECT " +
               "    e.EMP_ID, " +
               "    e.FIRST_NAME, " +
               "    e.LAST_NAME, " +
               "    d.DEPARTMENT_NAME, " +
               "    COUNT(y.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
               "FROM " +
               "    EMPLOYEE e " +
               "JOIN " +
               "    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
               "LEFT JOIN " +
               "    EMPLOYEE y ON e.DEPARTMENT = y.DEPARTMENT " +
               "              AND y.DOB > e.DOB " +
               "GROUP BY " +
               "    e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME " +
               "ORDER BY " +
               "    e.EMP_ID DESC";
    }
    
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        
        SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
        HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(solutionRequest, headers);
        
        try {
            System.out.println("Submitting solution to: " + webhookUrl);
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl, HttpMethod.POST, requestEntity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Solution submitted successfully!");
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("Failed to submit solution. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}