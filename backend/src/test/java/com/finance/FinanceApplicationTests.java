package com.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.dto.LoginRequest;
import com.finance.dto.RecordRequest;
import com.finance.entity.FinancialRecord.RecordType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FinanceApplicationTests {

    @Autowired MockMvc     mvc;
    @Autowired ObjectMapper mapper;

    static String adminToken;
    static String viewerToken;

    @Test @Order(1)
    void registerAdmin() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Test Admin","email":"tadmin@test.com",
                     "password":"pass123","role":"ADMIN"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test @Order(2)
    void loginAdmin() throws Exception {
        MvcResult res = mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"tadmin@test.com","password":"pass123"}
                """))
                .andExpect(status().isOk())
                .andReturn();

        adminToken = mapper.readTree(res.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test @Order(3)
    void registerViewer() throws Exception {
        MvcResult res = mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Test Viewer","email":"tviewer@test.com","password":"pass123"}
                """))
                .andExpect(status().isCreated())
                .andReturn();
        viewerToken = mapper.readTree(res.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test @Order(4)
    void adminCanCreateRecord() throws Exception {
        RecordRequest req = new RecordRequest();
        req.setAmount(BigDecimal.valueOf(1000));
        req.setType(RecordType.INCOME);
        req.setCategory("Salary");
        req.setDate(LocalDate.now());

        mvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test @Order(5)
    void viewerCannotCreateRecord() throws Exception {
        RecordRequest req = new RecordRequest();
        req.setAmount(BigDecimal.valueOf(500));
        req.setType(RecordType.EXPENSE);
        req.setCategory("Food");
        req.setDate(LocalDate.now());

        mvc.perform(post("/api/records")
                .header("Authorization", "Bearer " + viewerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test @Order(6)
    void viewerCanAccessDashboard() throws Exception {
        mvc.perform(get("/api/dashboard/summary")
                .header("Authorization", "Bearer " + viewerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.netBalance").exists());
    }

    @Test @Order(7)
    void unauthenticatedRequestFails() throws Exception {
        mvc.perform(get("/api/records"))
                .andExpect(status().isUnauthorized());
    }

    @Test @Order(8)
    void invalidLoginFails() throws Exception {
        mvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"tadmin@test.com","password":"wrongpassword"}
                """))
                .andExpect(status().isUnauthorized());
    }
}
