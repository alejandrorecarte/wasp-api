package org.example.waspapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseAuthService {

  private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthService.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${supabase.url}")
  private String supabaseUrl;

  @Value("${supabase.service-role-key}")
  private String serviceRoleKey;

  public void deleteUser(String userId) {
    String url = supabaseUrl + "/auth/v1/admin/users/" + userId;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceRoleKey);
    headers.set("apikey", serviceRoleKey);

    try {
      restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
      logger.info("Deleted Supabase Auth user: {}", userId);
    } catch (Exception e) {
      logger.error("Failed to delete Supabase Auth user {}: {}", userId, e.getMessage());
    }
  }
}
