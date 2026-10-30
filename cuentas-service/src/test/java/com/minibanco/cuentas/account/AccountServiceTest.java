package com.minibanco.cuentas.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    AccountService accountService;

    @Test
    void findById_whenAccountDoesNotExist_throwsException() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.findById(99L));
    }

    @Test
    void findById_whenAccountExist_throwsException() {

        // Creamos una cuenta para el test
        Account account = new Account("Ana", new BigDecimal("100.00"));

        // Cuando busquemos id=1, devolvemos la cuenta que hemos creado
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // Buscamos la cuenta con id = 1 y guardamos su valor en una variable
        AccountResponse response = accountService.findById(1L);

        // Comprobamos que el titular de la cuenta es "Ana"
        assertEquals("Ana", response.holder());

    }

    @Test
    void create_savesAccountAndReturnsResponse() {
        // Creamos una petición de creación de cuenta
        CreateAccountRequest request = new CreateAccountRequest("Ana", new BigDecimal("100.00"));

        // Cuando se registre una cuenta, devolvemos la misma cuenta que se ha creado
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Hacemos la petición y guardamos la respuesta (la cuenta creada)
        AccountResponse response = accountService.create(request);

        // Comprobamos que el titular es el mismo
        assertEquals("Ana", response.holder());
        // Verificamos que el servicio llamó a save()
        verify(accountRepository).save(any(Account.class));
    }
}
