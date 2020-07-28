package kr.co.kurtcobain.util;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.co.kurtcobain.domain.BoardFile;
import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.repository.UserRepository;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private final UserRepository userRepository;

    public BoardFile oneParseFile(MultipartFile multipartFile, UserPrincipal userPrincipal) throws IOException {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        if(ObjectUtils.isEmpty(multipartFile)){
            return null;
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        String path = "images/" + current.format(format);
        File file = new File(path);
        if(file.exists() == false){
            file.mkdirs();
        }


        String newFileName, originalFileExtension, contentType;

        if(multipartFile.isEmpty() == false) {
            contentType = multipartFile.getContentType();
            if (ObjectUtils.isEmpty(contentType)) {
                return null;
            } else {
                if (contentType.contains("image/jpeg")) {
                    originalFileExtension = ".jpg";
                } else if (contentType.contains("image/png")) {
                    originalFileExtension = ".png";
                } else if (contentType.contains("image/gif")) {
                    originalFileExtension = ".gif";
                } else {
                    return null;
                }
            }

            newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
            BoardFile boardFile = BoardFile.builder()
                    .user(user)
                    .path(path + "/" + newFileName)
                    .originalFileName(multipartFile.getOriginalFilename())
                    .size(multipartFile.getSize())
                    .build();


            file = new File(path + "/" + newFileName);
            multipartFile.transferTo(file);

            return boardFile;
        }
        return null;
    }


    public List<BoardFile> multiParseFile(MultipartHttpServletRequest multipartHttpServletRequest, UserPrincipal userPrincipal) throws IOException {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        if(ObjectUtils.isEmpty(multipartHttpServletRequest)){
            return null;
        }

        List<BoardFile> fileList = new ArrayList<>();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        String path = "images/"+current.format(format);
        File file = new File(path);
        if(file.exists() == false){
            file.mkdirs();
        }

        Iterator<String> iterator = multipartHttpServletRequest.getFileNames();

        String newFileName, originalFileExtension, contentType;

        while(iterator.hasNext()){
            List<MultipartFile> list = multipartHttpServletRequest.getFiles(iterator.next());
            for (MultipartFile multipartFile : list){
                if(multipartFile.isEmpty() == false){
                    contentType = multipartFile.getContentType();
                    if(ObjectUtils.isEmpty(contentType)){
                        break;
                    }
                    else{
                        if(contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        }
                        else if(contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        }
                        else if(contentType.contains("image/gif")) {
                            originalFileExtension = ".gif";
                        }
                        else{
                            break;
                        }
                    }

                    newFileName = Long.toString(System.nanoTime()) + originalFileExtension;
                    BoardFile boardFile = BoardFile.builder()
                            .user(user)
                            .path(path + "/" + newFileName)
                            .originalFileName(multipartFile.getOriginalFilename())
                            .size(multipartFile.getSize())
                            .build();

                    fileList.add(boardFile);

                    file = new File(path + "/" + newFileName);
                    multipartFile.transferTo(file);
                }
            }
        }
        return fileList;
    }
}

