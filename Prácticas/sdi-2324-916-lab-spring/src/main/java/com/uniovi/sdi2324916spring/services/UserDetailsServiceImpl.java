package com.uniovi.sdi2324916spring.services;

import com.uniovi.sdi2324916spring.entities.User;
import com.uniovi.sdi2324916spring.repositories.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException{
        User user = usersRepository.findByDni(dni);
        if(user == null){
            throw new UsernameNotFoundException(dni);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(
                user.getDni(),user.getPassword(),grantedAuthorities);
    }

}
