package kr.co.kurtcobain.util;

import kr.co.kurtcobain.domain.BoardFile;
import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.repository.UserRepository;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class S3FileUtils {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final UserRepository userRepository;

    // @PostConstruct : 의존성 주입이 이루어진 후 초기화를 수행하는 메서드
    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

//    public BoardFile upload(MultipartFile file, UserPrincipal userPrincipal) throws IOException {
//        String fileName = file.getOriginalFilename();
//
//        s3Client.putObject(new PutObjectRequest(bucket+"/upload", fileName, file.getInputStream(), null)
//                .withCannedAcl(CannedAccessControlList.PublicRead));
////        return s3Client.getUrl(bucket, fileName).toString();
//        return new BoardFile();
//    }

    public BoardFile upload(MultipartFile multipartFile, UserPrincipal userPrincipal) throws IOException {
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        if(ObjectUtils.isEmpty(multipartFile)){
            return null;
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime current = ZonedDateTime.now();
        String path = "/images/" + current.format(format);

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

            s3Client.putObject(new PutObjectRequest(bucket + path, newFileName, multipartFile.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            BoardFile boardFile = BoardFile.builder()
                    .user(user)
                    .path(path + "/" + newFileName)
                    .originalFileName(multipartFile.getOriginalFilename())
                    .size(multipartFile.getSize())
                    .build();

            return boardFile;
        }
        return null;
    }
}
