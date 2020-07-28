package kr.co.kurtcobain;

import kr.co.kurtcobain.config.AuthConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication(exclude = {MultipartAutoConfiguration.class})
@EnableConfigurationProperties(AuthConfig.class)
public class KurtcobainApplication {
    //표준시 Asia/Seoul로 설정
    @PostConstruct
    void initApplication() {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {

        SpringApplication.run(KurtcobainApplication.class, args);
    }

}
