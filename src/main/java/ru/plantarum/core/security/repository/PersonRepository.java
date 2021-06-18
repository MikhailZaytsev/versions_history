package ru.plantarum.core.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.plantarum.core.security.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByPersonName(String name);
}
