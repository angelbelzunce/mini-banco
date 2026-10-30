package com.minibanco.cuentas.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccounControllerTest {


    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AccountService accountService;

    @Test
    void findById_whenAccountDoesNotExist_returns404() throws Exception {

        // Cuando busquemos la cuenta con id = 99 lanzaremos la excepción
        when(accountService.findById(99L)).thenThrow(new AccountNotFoundException(99L));


        mockMvc.perform(get("/api/cuentas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenHolderIsBlank_returns400() throws Exception {

        mockMvc.perform(post("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"holder\":\"\",\"initialBalance\":100.00})"))
                .andExpect(status().isBadRequest());

    }

}
