package com.minibanco.cuentas.transfer;

import com.minibanco.cuentas.TestcontainersConfiguration;
import com.minibanco.cuentas.account.Account;
import com.minibanco.cuentas.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class TransferIntegrationTest {

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

        transferService.transfer("key-1", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00")));

        assertEquals(new BigDecimal("70.00"), accountRepository.findById(ana.getId()).get().getBalance());
        assertEquals(new BigDecimal("80.00"), accountRepository.findById(luis.getId()).get().getBalance());

    }

    @Test
    void transfer_withSameIdempotencyKey_movesOnlyOnce() {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("50.00")));


        transferService.transfer("same-key", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00")));
        transferService.transfer("same-key", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("30.00")));


        assertEquals(1, transferRepository.count());
        assertEquals(new BigDecimal("70.00"), accountRepository.findById(ana.getId()).get().getBalance());

    }

    @Test
    void transfer_concurrentDebits_onlyOneSucceds() throws InterruptedException {
        Account ana = accountRepository.save(new Account("Ana", new BigDecimal("100.00")));
        Account luis = accountRepository.save(new Account("Luis", new BigDecimal("0.00")));


        Thread t1 = new Thread(() -> {
            try {
                transferService.transfer("key-1", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("60.00")));
            } catch (Exception e) {
                // esperado: uno de los dos hilos falla
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                transferService.transfer("key-2", new CreateTransferRequest(ana.getId(), luis.getId(), new BigDecimal("60.00")));
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

}
