package com.encore.playground.global.service;

import com.encore.playground.domain.member.service.MemberSecurityService;
import com.encore.playground.global.dto.RefreshTokenDto;
import com.encore.playground.global.dto.TokenDto;
import com.encore.playground.global.jwt.JwtTokenProvider;
import com.encore.playground.global.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class TokenService {
    private final MemberSecurityService memberSecurityService;
    private final RefreshRepository refreshRepository;


    /**
     * 유저 아이디를 통해 아이디와 유저 권한 값을 넣어 토큰을 생성해준다.
     * @param loginId
     * @return token dto
     */

    public TokenDto generateToken(String loginId) {
        UserDetails member = memberSecurityService.loadUserByUsername(loginId);

        // 토큰 생성할 때 유저 아이디와 권한을 넣어준다.
        String userid = member.getUsername();
        String roles = member.getAuthorities().stream().toList().get(0).toString();
        TokenDto tokenDto = JwtTokenProvider.generateToken(userid, roles);
        RefreshTokenDto refreshTokenDto = RefreshTokenDto
                .builder()
                .refreshToken(tokenDto.getRefreshToken())
                .memberId(tokenDto.getKey())
                .build();
        saveRefreshToken(refreshTokenDto);
        return tokenDto;
    }

    // refreshToken을 DB에 저장하기
    public void saveRefreshToken(RefreshTokenDto refreshTokenDto) {
        refreshRepository.save(refreshTokenDto.toEntity());

    }
}
