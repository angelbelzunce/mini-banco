package com.minibanco.cuentas.transfer;

import com.minibanco.cuentas.account.Account;
import com.minibanco.cuentas.account.AccountNotFoundException;
import com.minibanco.cuentas.account.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    TransferRepository transferRepository;

    @Mock
    AccountRepository accountRepository;

    @InjectMocks
    TransferService transferService;

    @Test
    void findById_whenTransferDoesNotExist_throwsTransferNotFoundException() {
        when(transferRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransferNotFoundException.class, () -> transferService.findById(99L));
    }

    @Test
    void findById_whenTransferExists_returnsExistingTransfer() {
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("30.00"), "key-1", LocalDateTime.now(), "COMPLETED");

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        TransferResponse response = transferService.findById(1L);

        assertEquals(new BigDecimal("30.00"), response.amount());

    }

    @Test
    void findById_whenTransferExists_returnsStatus() {
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("30.00"), "key-1", LocalDateTime.now(), "COMPLETED");

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        TransferResponse response = transferService.findById(1L);

        assertEquals("COMPLETED", response.status());
    }

    @Test
    void findById_whenTransferExists_returnsSourceAccount() {
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("30.00"), "key-1", LocalDateTime.now(), "COMPLETED");

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        TransferResponse response = transferService.findById(1L);

        assertEquals(1L, response.sourceAccountId());
    }

    @Test
    void findById_whenTransferExists_returnsTargetAccount(){
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("30.00"), "key-1", LocalDateTime.now(), "COMPLETED");

        when(transferRepository.findById(1L)).thenReturn(Optional.of(transfer));

        TransferResponse response = transferService.findById(1L);

        assertEquals(2L, response.targetAccountId());
    }

    @Test
    void transfer_whenIdempotencyKeyWasUsed_returnsExistingTransfer() {
        Transfer transfer = new Transfer(1L, 2L, new BigDecimal("30.00"), "key-1", LocalDateTime.now(), "COMPLETED");

        when(transferRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(transfer));

        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("30.00"), TransferType.INTERNAL);

        TransferResponse response = transferService.transfer("key-1", request);

        assertEquals(new BigDecimal("30.00"), response.amount());
        verify(accountRepository, never()).findById(any());

    }

    @Test
    void transfer_whenSourceAccountDoesNotExist_throwsAccountNotFoundException(){
        // Creamos una petición
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("30.00"), TransferType.INTERNAL);

        // Indicamos al repositorio que cuando busque una cuenta con id 1, devuelva un optional vacío
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // Comprobamos que cuando el servicio envíe la petición que hemos creado en el paso 1, se lance la excepción
        assertThrows(AccountNotFoundException.class, () -> transferService.transfer("key-1", request));

    }

    @Test
    void transfer_whenTargetAccountDoesNotExist_throwsAccountNotFoundException() {
        // Creamos una petición
        CreateTransferRequest request = new CreateTransferRequest(1L, 2L, new BigDecimal("30.00"), TransferType.INTERNAL);

        // La primera cuenta que busca el método (la cuenta de origen) la simulamos para que no falle al buscarla, ya que la que nos interesa mirar es la de destino
        when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account("Ana", new BigDecimal("100.00"))));
        // La cuenta de destino no debe encontrarla (devolvemos un Optional vacío
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Comprobamos que lanza la excepcion
        assertThrows(AccountNotFoundException.class, () -> transferService.transfer("key-1", request));

    }

}
