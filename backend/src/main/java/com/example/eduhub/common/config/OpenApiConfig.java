package com.example.eduhub.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** NFR-04: OpenAPI 문서의 기본 정보. 문서는 /v3/api-docs, 화면은 /swagger-ui.html. */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI eduhubOpenApi() {
    return new OpenAPI()
        .info(new Info().title("EduHub API").description("사내 교육과정 조회 및 수강 신청 API").version("v1"));
  }
}
