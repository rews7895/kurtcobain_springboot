package kr.co.kurtcobain.controller;

import kr.co.kurtcobain.domain.BoardFile;
import kr.co.kurtcobain.security.CurrentUser;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
 * File 입출력을 담당할 컨트롤러로 에디터에 올리는 이미지 처리를 담당
 * 2020-07-03 PJS
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FileController {
    private final FileService fileService;

    @PostMapping("/file")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> saveFile(@CurrentUser UserPrincipal userPrincipal, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        // local
        BoardFile getFile = fileService.saveFile(multipartFile, userPrincipal);
        // s3
//        BoardFile getFile = fileService.saveS3File(multipartFile, userPrincipal);
        return ResponseEntity.ok(getFile);
    }

}
