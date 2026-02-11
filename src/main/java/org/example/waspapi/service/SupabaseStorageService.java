package org.example.waspapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseStorageService {

  private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${supabase.url}")
  private String supabaseUrl;

  @Value("${supabase.service-role-key}")
  private String serviceRoleKey;

  public String upload(String bucket, String path, byte[] data, String contentType) {
    String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceRoleKey);
    headers.set("apikey", serviceRoleKey);
    headers.setContentType(MediaType.parseMediaType(contentType));
    headers.set("x-upsert", "true");

    restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(data, headers), String.class);
    logger.info("Uploaded file to {}/{}", bucket, path);
    return path;
  }

  public void delete(String bucket, String path) {
    String url = supabaseUrl + "/storage/v1/object/" + bucket;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceRoleKey);
    headers.set("apikey", serviceRoleKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    String body = "{\"prefixes\":[\"" + path + "\"]}";
    restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(body, headers), String.class);
    logger.info("Deleted file from {}/{}", bucket, path);
  }

  public String getPublicUrl(String bucket, String path) {
    return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
  }
}
