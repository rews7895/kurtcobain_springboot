package kr.co.kurtcobain.service;

import kr.co.kurtcobain.domain.BoardFile;
import kr.co.kurtcobain.repository.FileRepository;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.FileUtils;
import kr.co.kurtcobain.util.S3FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/*
 * File 입출력을 담당할 서비스 로직 관련 부분
 * 2020-07-03 PJS
 */
@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;

    private final FileUtils fileUtils;

    private final S3FileUtils s3FileUtils;

    // filesystem save
    public BoardFile saveFile(MultipartFile multipartFile, UserPrincipal userPrincipal) throws IOException {

        BoardFile boardFile = fileUtils.oneParseFile(multipartFile, userPrincipal);

        return fileRepository.save(boardFile);
    }
    // s3 save
    public BoardFile saveS3File(MultipartFile multipartFile, UserPrincipal userPrincipal) throws IOException {

        BoardFile boardFile = s3FileUtils.upload(multipartFile, userPrincipal);

        return fileRepository.save(boardFile);
    }
}
