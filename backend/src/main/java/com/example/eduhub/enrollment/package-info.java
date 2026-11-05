/**
 * 수강 신청(enrollment) 도메인: 신청·대기·취소·대기 승급·수료를 담당한다 (FR-04~09).
 *
 * <p>내부 계층: api(컨트롤러) → service → repository, domain(엔티티·값 객체).
 *
 * <p>의존 규칙: course 도메인은 course의 service를 통해서만 사용하고, course의 repository를 직접 쓰지 않는다.
 */
package com.example.eduhub.enrollment;
