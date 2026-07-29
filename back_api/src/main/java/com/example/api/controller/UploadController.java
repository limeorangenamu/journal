package com.example.api.controller;

import com.example.api.dto.UploadResultDTO;
//import com.example.api.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
@RequiredArgsConstructor
public class UploadController {
//  private final MovieService movieService;

  @Value("${com.example.upload.path}")
  private String uploadPath;

  @PostMapping("/uploadAjax")
  public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {
    // 전송결과를 리턴해주기 위한 객체
    List<UploadResultDTO> uploadResultDTOList = new ArrayList<>();

    for (MultipartFile uploadFile : uploadFiles) {
      // 업로드 파일이 이미지가 아닐 경우 forbidden
      if (!uploadFile.getContentType().startsWith("image")) {
        log.warn("This file is not image type.");
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }

      // 실제 파일 이름::IE, Edge는 전체 경로가 넘어오기 때문
      String originalName = uploadFile.getOriginalFilename(); // 경로포함 파일명
      String fileName = originalName.substring(originalName.lastIndexOf("\\") + 1);
      log.info("fileName: " + fileName); // 실제 파일명만 출력

      // 저장될 경로 생성 :: c:\\upload\\2026\\05\\18
      String folderPath = makeFolder();
      // 유니크한 파일명을 위한 uuid
      String uuid = UUID.randomUUID().toString();

      // 실제 서버 저장될 경로와 파일명 c:\\upload\\2026\\05\\18\\uuid_fileName
      String saveName = uploadPath + File.separator
          + folderPath + File.separator + uuid + "_" + fileName;
      Path savePath = Paths.get(saveName);// saveName을 실제 파일로 생성준비하는 객체

      try {
        uploadFile.transferTo(savePath); // 원본 이미지를 지정 경로에 생성
        String thumbnailSaveName = uploadPath + File.separator
            + folderPath + File.separator + "s_" + uuid + "_" + fileName;
        File thumbnailFile = new File(thumbnailSaveName);// 파일 생성위한 객체 생성
        if (!thumbnailFile.exists()) {
          Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 400, 300);
        }
        uploadResultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ResponseEntity<>(uploadResultDTOList, HttpStatus.OK);
  }

  private String makeFolder() {
    String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    // '/'를 운영체제에 맞게 파일 구분자로 변경
    String folderPath = str.replace("/", File.separator);
    File uploadPathFolder = new File(uploadPath, folderPath);
    if (!uploadPathFolder.exists()) uploadPathFolder.mkdirs();

    return folderPath;
  }

  @GetMapping("/display")
  public ResponseEntity<byte[]> getFile(String fileName, String size) {
    ResponseEntity<byte[]> result = null;
    try {
      String srcFileName = URLDecoder.decode(fileName, "UTF-8");
      File file = new File(uploadPath + File.separator + srcFileName);

      if (size != null && size.equals("1")) {
        log.info("file: " + file.getName());
        // thumbnail의 파일명 s_를 제거하고 들고 오겠다는 것
        file = new File(file.getParent(), file.getName().substring(2));
      }

      HttpHeaders headers = new HttpHeaders(); //브라우저에 전송할때 Header 필요
      headers.add("Content-Type", Files.probeContentType(file.toPath()));//파일 타입설정
      result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
    }catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @PostMapping("/removeFile")
  public ResponseEntity<Boolean> removeFile(String fileName, String uuid) {
    log.info(">>>"+fileName);
    String srcFileName = null;

//    if (uuid != null) movieService.removeMovieImagebyUUID(uuid);

    try {
      srcFileName = URLDecoder.decode(fileName, "UTF-8"); //정확한 소스파일명
      File file = new File(uploadPath+File.separator+srcFileName);//해당되는 파일선택
      boolean result = file.delete(); // 첫번째 원본 파일 지움.
      File thumbnail = new File(file.getParent(), "s_" + file.getName());
      result = thumbnail.delete() && result; // 두번째 썸내일 파일 지움
      return new ResponseEntity<>(result,
          result ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
