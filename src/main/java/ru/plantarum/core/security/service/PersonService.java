package ru.plantarum.core.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.plantarum.core.security.entity.Role;
import ru.plantarum.core.security.entity.Person;
import ru.plantarum.core.security.repository.RoleRepository;
import ru.plantarum.core.security.repository.PersonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Person person = personRepository.findByPersonName(name);

        if (person == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return person;
    }

    public Person findUserById(Long userId) {
        Optional<Person> userFromDb = personRepository.findById(userId);
        return userFromDb.orElse(new Person());
    }

    public List<Person> allPersons() {
        return personRepository.findAll();
    }

    public boolean savePerson(Person person) {
        Person personFromDb = personRepository.findByPersonName(person.getUsername());

        if (personFromDb != null) {
            return false;
        }

        person.setRoles(Collections.singleton(roleRepository.findByRoleName(DEFAULT_ROLE)));
        person.setPersonPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        personRepository.save(person);
        return true;
    }

    public boolean deletePerson(Long userId) {
        if (personRepository.findById(userId).isPresent()) {
            personRepository.deleteById(userId);
            return true;
        }
        return false;
    }

}
