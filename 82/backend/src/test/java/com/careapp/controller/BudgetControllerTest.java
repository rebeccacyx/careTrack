package com.careapp.controller;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.service.BudgetCalculationService;
import com.careapp.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private BudgetCalculationService budgetCalculationService;

    private Budget mockBudget;

    @BeforeEach
    void setUp() {
        mockBudget = new Budget();
        mockBudget.setId("b1");
        mockBudget.setPatientId("p1");
        mockBudget.setTotalBudget(1000.0);
    }

    // ---------------------- CREATE ----------------------
    @Test
    void testCreateBudgetSuccess() throws Exception {
        Mockito.when(budgetService.createBudget(Mockito.any(Budget.class)))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"totalBudget\":1000}";

        mockMvc.perform(post("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Budget created successfully!"));
    }

    @Test
    void testCreateBudgetFailure() throws Exception {
        Mockito.when(budgetService.createBudget(Mockito.any()))
                .thenThrow(new RuntimeException("DB error"));

        String body = "{\"patientId\":\"p1\"}";

        mockMvc.perform(post("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.containsString("Failed to create budget")));
    }

    // ---------------------- GET ----------------------
    @Test
    void testGetBudgetByPatientSuccess() throws Exception {
        Mockito.when(budgetService.getBudgetByPatientId("p1"))
                .thenReturn(mockBudget);

        mockMvc.perform(get("/api/budget/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.patientId").value("p1"))
                .andExpect(jsonPath("$.msg").value("Budget retrieved successfully!"));
    }

    @Test
    void testGetBudgetByPatientFailure() throws Exception {
        Mockito.when(budgetService.getBudgetByPatientId("p1"))
                .thenThrow(new RuntimeException("not found"));

        mockMvc.perform(get("/api/budget/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"));
    }

    // ---------------------- UPDATE ----------------------
    @Test
    void testUpdateBudgetSuccess() throws Exception {
        Mockito.when(budgetService.updateBudget(Mockito.any(Budget.class)))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"totalBudget\":2000}";

        mockMvc.perform(put("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Budget updated successfully!"));
    }

    // ---------------------- DELETE ----------------------
    @Test
    void testDeleteBudgetSuccess() throws Exception {
        Mockito.when(budgetService.deleteBudget("p1"))
                .thenReturn(true);

        mockMvc.perform(delete("/api/budget/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Budget deleted successfully!"));
    }

    @Test
    void testDeleteBudgetNotFound() throws Exception {
        Mockito.when(budgetService.deleteBudget("p1"))
                .thenReturn(false);

        mockMvc.perform(delete("/api/budget/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"));
    }

    // ---------------------- ADJUST TOTAL ----------------------
    @Test
    void testAdjustTotalBudgetSuccess() throws Exception {
        Mockito.when(budgetService.adjustTotalBudget("p1", 1500.0, "increase"))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"newTotalBudget\":1500,\"reason\":\"increase\"}";

        mockMvc.perform(post("/api/budget/adjust-total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Total budget adjusted successfully!"));
    }

    // ---------------------- REALLOCATE ----------------------
    @Test
    void testReallocateBetweenCategoriesSuccess() throws Exception {
        Mockito.when(budgetService.reallocateBetweenCategories(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"fromCategoryId\":\"c1\",\"toCategoryId\":\"c2\",\"amount\":100,\"reason\":\"move\"}";

        mockMvc.perform(post("/api/budget/reallocate-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Category reallocation completed!"));
    }

    @Test
    void testReallocateBetweenSubElementsSuccess() throws Exception {
        Mockito.when(budgetService.reallocateBetweenSubElements(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"categoryId\":\"c1\",\"fromSubElementId\":\"s1\",\"toSubElementId\":\"s2\",\"amount\":50,\"reason\":\"shift\"}";

        mockMvc.perform(post("/api/budget/reallocate-subelement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Sub-element reallocation completed!"));
    }

    // ---------------------- REFUND ----------------------
    @Test
    void testRefundSubElementSuccess() throws Exception {
        Mockito.when(budgetService.refundSubElement(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                        Mockito.anyDouble(), Mockito.anyString()))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"categoryId\":\"c1\",\"subElementId\":\"s1\",\"amount\":20,\"reason\":\"refund\"}";

        mockMvc.perform(post("/api/budget/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Refund processed successfully!"));
    }

    // ---------------------- CATEGORY ----------------------
    @Test
    void testAddCategorySuccess() throws Exception {
        Mockito.when(budgetService.addCategory(Mockito.anyString(), Mockito.any(BudgetCategory.class)))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"name\":\"Health\",\"description\":\"desc\",\"categoryBudget\":100}";

        mockMvc.perform(post("/api/budget/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Category added successfully!"));
    }

    // ---------------------- SUB-ELEMENT ----------------------
    @Test
    void testAddSubElementSuccess() throws Exception {
        Mockito.when(budgetService.addSubElement(Mockito.anyString(), Mockito.anyString(), Mockito.any(BudgetSubElement.class)))
                .thenReturn(mockBudget);

        String body = "{\"patientId\":\"p1\",\"categoryId\":\"c1\",\"name\":\"Toothpaste\",\"description\":\"desc\",\"subElementBudget\":20}";

        mockMvc.perform(post("/api/budget/sub-element")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Sub-element added successfully!"));
    }

    // ---------------------- VALIDATE ----------------------
    @Test
    void testValidateBudgetSuccess() throws Exception {
        Mockito.when(budgetCalculationService.validateBudget(Mockito.any()))
                .thenReturn(Arrays.asList());

        mockMvc.perform(post("/api/budget/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patientId\":\"p1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Budget validation passed!"));
    }

    // ---------------------- WARNINGS ----------------------
    @Test
    void testGetBudgetWarnings() throws Exception {
        Mockito.when(budgetService.getBudgetByPatientId("p1"))
                .thenReturn(mockBudget);
        Mockito.when(budgetCalculationService.getBudgetWarnings(mockBudget))
                .thenReturn(Arrays.asList("Overspending"));

        mockMvc.perform(get("/api/budget/warnings/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("Overspending"));
    }

    // ---------------------- STATISTICS ----------------------
    @Test
    void testGetBudgetStatistics() throws Exception {
        Mockito.when(budgetService.getBudgetByPatientId("p1")).thenReturn(mockBudget);
        Mockito.when(budgetCalculationService.calculateTotalUsed(mockBudget)).thenReturn(200.0);
        Mockito.when(budgetCalculationService.calculateTotalBalance(mockBudget)).thenReturn(800.0);
        Mockito.when(budgetCalculationService.calculateUsagePercentage(mockBudget)).thenReturn(20.0);
        Mockito.when(budgetCalculationService.isOverBudget(mockBudget)).thenReturn(false);
        Mockito.when(budgetCalculationService.getBudgetWarnings(mockBudget)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/budget/statistics/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalBudget").value(1000.0))
                .andExpect(jsonPath("$.msg").value("Budget statistics retrieved successfully!"));
    }
}
