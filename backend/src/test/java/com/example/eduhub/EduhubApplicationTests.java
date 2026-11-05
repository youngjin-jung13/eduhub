package com.example.eduhub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** test 프로필로 전체 컨텍스트(DataSource, JPA, Flyway 포함)가 뜨는지 확인한다. (NFR-06) */
@SpringBootTest
@ActiveProfiles("test")
class EduhubApplicationTests {

  @Test
  void contextLoads() {}
}
