package com.minibanco.cuentas.transfer;

import com.minibanco.cuentas.TestcontainersConfiguration;
import com.minibanco.cuentas.account.Account;
import com.minibanco.cuentas.account.AccountRepository;
import com.minibanco.cuentas.account.InsufficientFundsException;
import com.minibanco.cuentas.event.TransferEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class TransferIntegrationTest {

    @MockitoBean
    TransferEventPublisher transferEventPublisher;

    @Autowired
    TransferService transferService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransferRepository transferRepository;

    @BeforeEach
    void cleanDatabase() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void transfer_moveMoneyBetweenAccounts() {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("50.00")));

        transferService.transfer("key-1", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00"), TransferType.INTERNAL));

        assertEquals(new BigDecimal("70.00"), accountRepository.findById(ana.getId()).get().getBalance());
        assertEquals(new BigDecimal("80.00"), accountRepository.findById(luis.getId()).get().getBalance());

    }

    @Test
    void transfer_withSameIdempotencyKey_movesOnlyOnce() {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("50.00")));


        transferService.transfer("same-key", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00"), TransferType.INTERNAL));
        transferService.transfer("same-key", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00"), TransferType.INTERNAL));


        assertEquals(1, transferRepository.count());
        assertEquals(new BigDecimal("70.00"), accountRepository.findById(ana.getId()).get().getBalance());

    }

    @Test
    void transfer_concurrentDebits_onlyOneSucceeds() throws InterruptedException {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("0.00")));


        Thread t1 = new Thread(() -> {
            try {
                transferService.transfer("key-1", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("60.00"), TransferType.INTERNAL));
            } catch (Exception e) {
                // esperado: uno de los dos hilos falla
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                transferService.transfer("key-2", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("60.00"), TransferType.INTERNAL));
            } catch (Exception e) {
                // esperado: uno de los dos hilos falla
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertEquals(1, transferRepository.count());
        assertEquals(new BigDecimal("40.00"), accountRepository.findById(ana.getId()).get().getBalance());
    }

    @Test
    void transfer_whenExternal_debitsAmountPlusCommission() {

        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("50.00")));

        transferService.transfer("external-key", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("50.00"), TransferType.EXTERNAL));

        assertEquals(new BigDecimal("49.50"), accountRepository.findById(ana.getId()).get().getBalance());
        assertEquals(new BigDecimal("100.00"), accountRepository.findById(luis.getId()).get().getBalance());
    }

    @Test
    void transfer_whenBalanceDoesNotCoverCommission_throwsInsufficientFundsException() {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("50.00")));

        assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer("insufficient-funds-key",
                        new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("100.00"), TransferType.EXTERNAL)));
        assertEquals(new BigDecimal("100.00"), accountRepository.findById(ana.getId()).get().getBalance());
        assertEquals(new BigDecimal("50.00"), accountRepository.findById(luis.getId()).get().getBalance());
        assertEquals(0, transferRepository.count());

    }
}
