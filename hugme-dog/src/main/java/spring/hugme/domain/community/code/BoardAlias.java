package spring.hugme.domain.community.code;

import java.util.Arrays;
import java.util.Optional;

public enum BoardAlias {

  FREE("free", "자유게시판"),
  QNA("qna", "질문답변"),
  NOTICE("notice", "공지사항");

  private final String path;
  private final String displayName;

  BoardAlias(String path, String displayName) {
    this.path = path;
    this.displayName = displayName;
  }

  public String getPath() {
    return path;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * URL path 문자열(예: "free")을 받아 해당하는 BoardAlias enum 상수를 찾습니다.
   * @param path - URL에서 추출한 별칭
   * @return Optional<BoardAlias> (찾으면 해당 enum, 못찾으면 Optional.empty())
   */
  public static Optional<BoardAlias> findByPath(String path) {
    // enum의 모든 상수를 순회(stream)하면서
    return Arrays.stream(values())
        // path 값이 일치하는 것을 찾습니다.
        .filter(alias -> alias.getPath().equals(path))
        // 첫 번째 일치하는 것을 반환합니다.
        .findFirst();
  }
}