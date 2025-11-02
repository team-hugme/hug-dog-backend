/**
 * infra 계층ㄴ
 * 외부 시스템 및 인프라 구성 요소를 관리하는 계층입니다.
 *
 * <p>예시 구성:</p>
 * <ul>
 *     <li><b>config</b> — Redis, Security 등 외부 설정</li>
 *     <li><b>jwt</b> — JWT 토큰 발급 및 인증 관련 구성</li>
 *     <li><b>id</b> — Snowflake, UUID 등 ID 생성 로직</li>
 *     <li><b>redis</b> — Redis 캐시 설정 및 서비스</li>
 * </ul>
 *
 * <p>이 계층은 외부 의존성과의 통신을 담당하며,
 * 도메인 로직에 직접 영향을 주지 않습니다.</p>
 */
package spring.hugme.infra;
