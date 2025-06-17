package org.example.waspapi.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HolaService {

  @Async
  public CompletableFuture<String> saludarAsync() {
    // Simulamos que tarda algo
    try {
      Thread.sleep(3000); // 3 segundos
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return CompletableFuture.completedFuture("¡Hola desde Spring Boot, en segundo plano máquina!");
  }
}
