package ru.plantarum.core.security.service;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.plantarum.core.security.entity.Role;
import ru.plantarum.core.security.entity.Person;
import ru.plantarum.core.security.repository.RoleRepository;
import ru.plantarum.core.security.repository.PersonRepository;
import ru.plantarum.core.utils.search.CriteriaUtils;
import ru.plantarum.core.utils.search.SearchCriteria;
import ru.plantarum.core.web.paging.Direction;
import ru.plantarum.core.web.paging.Order;
import ru.plantarum.core.web.paging.PagingRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private CriteriaUtils criteriaUtils;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Person person = personRepository.findByPersonName(name);


        if (person == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return person;
    }

    public ru.plantarum.core.web.paging.Page<Person> findAll(PagingRequest pagingRequest) {

        final List<SearchCriteria> criteriaList = pagingRequest.getColumns()
                .stream().filter(c -> !(c.getSearch().getValue().isEmpty()))
                .map(column -> new SearchCriteria(column.getData(),
                        SearchCriteria.OPERATION_EQUALS, column.getSearch().getValue())
                ).collect(Collectors.toList());

        final Predicate predicates = criteriaUtils.getPredicate(criteriaList,
                Person.class, "person");

        int pageNumber = pagingRequest.getStart() / pagingRequest.getLength();
        Order order = pagingRequest.getOrder().stream()
            .findFirst()
            .orElse(new Order(0, Direction.desc));
        String colToOrder = pagingRequest.getColumns().get(order.getColumn()).getData();
        final PageRequest pageRequest = PageRequest.of(pageNumber, pagingRequest.getLength(),
                Sort.Direction.fromString(order.getDir().name()), colToOrder);
        final Page<Person> persons = personRepository.findAll(predicates, pageRequest);
        ru.plantarum.core.web.paging.Page<Person> page = new ru.plantarum.core.web.paging.Page<Person>(persons);
        page.setDraw(pagingRequest.getDraw());
        return page;
    }

    public Person findPersonById(Long personId) {
        Optional<Person> userFromDb = personRepository.findById(personId);
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

    public Optional<Person> getOne(Long id) {
        return Optional.of(personRepository.getOne(id));
    }

    public boolean deletePerson(Long userId) {
        if (personRepository.findById(userId).isPresent()) {
            personRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
