package kr.co.kurtcobain.service;

import kr.co.kurtcobain.domain.User;
import kr.co.kurtcobain.domain.enums.AuthProvider;
import kr.co.kurtcobain.security.UserPrincipal;
import kr.co.kurtcobain.security.oauth2.user.OAuth2UserInfo;
import kr.co.kurtcobain.security.oauth2.user.OAuth2UserInfoFactory;
import kr.co.kurtcobain.util.exception.OAuth2AuthenticationProcessingException;
import org.springframework.util.StringUtils;
import kr.co.kurtcobain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/*
 * OAuth2 인증 된 유저정보 처리
 * 2020-06-17 PJS
 * 1. line.34 OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest); 이슈 (2020-06-24)
 *   - userNameAttribute을 설정해주지 않으면 DefaultOAuth2UserService의 line.94, 106에 있는 userNameAttributeName, request에서 걸림
 * 2. User Attributes에 이메일이 포함되어있지 않은 이슈 (2020-06-25)
 *   - 카카오의 경우 검수 전까지 이메일을 필수로 받을 수 없다. 검수 후에도 이메일이 필수가 아니라던가 하는 이유로 반려되면 현재 로직에서 사용 못하는 이슈 존재
 *   - 페이스북에 경우 핸드폰번호로 가입을 했을 경우 이메일이 attributes에 나타나지 않는 이슈가 있다. 이 경우 페이스북 개인 정보 설정에서 이메일을 추가해야 이메일을 attributes로 받을 수 있다.
 *   - 깃허브의 경우 public email을 설정하지 않은 경우 attributes에 email이 나오지 않는다.
 * 3. 현재 구현은 email unique환경에서의 기본 구성
 *   - 페이스북과 깃허브의 경우는 실제 구현시 email이 없는 계정이 존재 할 수 있다.
 *   - 따라서 페이스북을 사용해야하는 소셜로그인 업무를 받으면 54라인 if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {를 지우고
 *   - 69번라인 findByEmail 대신 findByProviderAndProviderId를 사용해야한다.
 *   - ex) SELECT * FROM users WHERE provider = 'facebook' AND provider_id = '111111111';
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        // User Attributes에 이메일이 포함되어있지 않은 경우
        if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            if(oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(AuthProvider.facebook.toString())) {
                throw new OAuth2AuthenticationProcessingException("code001");
            } else if (oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(AuthProvider.github.toString())) {
                throw new OAuth2AuthenticationProcessingException("code002");
            } else if(oAuth2UserRequest.getClientRegistration().getRegistrationId().equals(AuthProvider.kakao.toString())) {
                throw new OAuth2AuthenticationProcessingException("code003");
            } else {
                throw new OAuth2AuthenticationProcessingException("code004");
//                throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
            }
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        //다른 공급자에서 동일한 이메일을 통해 가입을 한 경우 걸러낸다
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
//                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
//                        user.getProvider() + " account. Please use your " + user.getProvider() +
//                        " account to login.");
                throw new OAuth2AuthenticationProcessingException("code005" + user.getProvider());
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    // provider를 거친 후 유저 정보가 없으면 정보 저장
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setName(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }

    // provider를 거친 후 유저 정보가 있을 시 이름과 이미지만 변경
    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setUpdatedDatetime(LocalDateTime.now());
        return userRepository.save(existingUser);
    }

}
