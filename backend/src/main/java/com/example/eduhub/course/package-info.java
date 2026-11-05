/**
 * 교육과정(course) 도메인: 과정과 회차의 등록·수정·종료·조회를 담당한다 (FR-01~03).
 *
 * <p>내부 계층: api(컨트롤러) → service → repository, domain(엔티티·값 객체).
 *
 * <p>의존 규칙: course는 enrollment를 참조하지 않는다. 의존 방향은 enrollment → course 한쪽뿐이다.
 */
package com.example.eduhub.course;
