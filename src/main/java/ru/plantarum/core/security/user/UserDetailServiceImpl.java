package ru.plantarum.core.security.user;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.plantarum.core.security.entity.Person;
import ru.plantarum.core.security.repository.PersonRepository;

@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Person person = personRepository.findByPersonName(name).orElseThrow(() ->
                    new UsernameNotFoundException("User doesn't t exists"));
        return SecurityUser.fromPerson(person);
    }
}
