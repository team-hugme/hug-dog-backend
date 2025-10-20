/**
 * domain 계층: 비즈니스 로직, 엔티티, 서비스, 컨트롤러를 포함합니다.
 * 비즈니스 로직을 담당하는 핵심 계층입니다.
 *
 * <p>주요 구성:</p>
 * <ul>
 *     <li><b>controller</b> — API 요청 처리</li>
 *     <li><b>repository</b> — DB와 서비스 사이 연결</li>
 *     <li><b>service</b> — 비즈니스 로직 수행</li>
 *     <li><b>dto</b> — 데이터 전송 객체</li>
 *     <li><b>entity</b> — JPA 엔티티 정의</li>
 * </ul>
 *
 * <p>각 도메인(auth, user, home 등)은 독립적으로 구성되어 있으며,
 * 내부적으로는 repository, service, controller, entity, dto 구조를 따릅니다.</p>
 */
package spring.hugme.domain;
