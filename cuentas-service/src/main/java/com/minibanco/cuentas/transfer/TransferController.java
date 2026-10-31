package com.minibanco.cuentas.transfer;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@RequestHeader("Idempotency-Key") String idempotencyKey, @RequestBody @Valid CreateTransferRequest request) {
        TransferResponse response = transferService.transfer(idempotencyKey, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> findById(@PathVariable Long id) {

        TransferResponse response = transferService.findById(id);

        return ResponseEntity.ok(response);

    }
}
