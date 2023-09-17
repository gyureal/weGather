package com.example.wegather.auth;

import com.example.wegather.global.vo.MemberType;
import com.example.wegather.member.domain.entity.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Builder
public class MemberDetails implements UserDetails {
  private final Long id;
  private final String username;
  private final String password;
  private final MemberType role;

  public static MemberDetails from(Member member) {
    return MemberDetails.builder()
        .id(member.getId())
        .username(member.getUsername())
        .password(member.getPassword())
        .role(member.getMemberType())
        .build();
  }

  public Boolean isAdmin() {
    return role == MemberType.ROLE_ADMIN;
  }

  public Long getMemberId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role.name()));
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
