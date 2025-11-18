package spring.hugme.domain.image.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.hugme.global.error.image.ImageUploadException;
import spring.hugme.global.error.image.InvalidFileFormatException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  @Value("${upload.path}")
  private String uploadDir; // application.properties 에서 설정한 경로

  public List<String> upload(List<MultipartFile> files) {
    if (files == null || files.isEmpty()) {
      throw new InvalidFileFormatException("업로드할 이미지가 없습니다.");
    }

    List<String> savedPaths = new ArrayList<>();

    String editorDirPath = uploadDir + File.separator;
    File editorDir = new File(editorDirPath);
    if (!editorDir.exists()) {
      editorDir.mkdirs(); // 경로 없으면 생성
    }

    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        throw new InvalidFileFormatException("비어있는 파일이 포함되어 있습니다.");
      }

      // 파일 타입 검사 (이미지 형식만 허용)
      String contentType = file.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
        throw new InvalidFileFormatException("이미지 파일만 업로드할 수 있습니다.");
      }

      String originalName = file.getOriginalFilename();
      String extension = getExtension(originalName);
      String renamed = UUID.randomUUID().toString() + "." + extension;

      File dest = new File(editorDir, renamed);

      try {
        file.transferTo(dest);
      } catch (IOException e) {
        throw new ImageUploadException("이미지 업로드 중 서버 오류가 발생했습니다.");
      }

      // 웹에서 접근 가능한 경로 (WebMvcConfigurer 에서 매핑된 것 기준)
      savedPaths.add("/images/" + renamed);
    }

    return savedPaths;
  }

  private String getExtension(String fileName) {
    // 확장자가 없거나 or .으로 끝나거나 or .으로 시작하는 경우
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex < 0 || dotIndex == fileName.length() - 1 || dotIndex == 0) {
      // 확장자가 없는 파일은 InvalidFileFormatException 에서 걸러지므로
      // 여기서는 정상적인 확장자만 처리하도록 설계
      throw new InvalidFileFormatException("유효하지 않은 파일명 또는 확장자입니다: " + fileName);
    }
    return fileName.substring(dotIndex + 1);
  }
}
