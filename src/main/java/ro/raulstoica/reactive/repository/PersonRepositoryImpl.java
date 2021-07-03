package ro.raulstoica.reactive.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.raulstoica.reactive.domain.Person;

import java.util.Objects;

public class PersonRepositoryImpl implements PersonRepository {

    //Mocking the database
    Person raul = new Person(1, "Raul", "Stoica");
    Person shrek = new Person(2, "Shrek", "Green");
    Person hulk = new Person(3, "Hulk", "Smash");
    Person alexander = new Person(4, "Alexander", "The Great");

    @Override
    public Mono<Person> getById(Integer id) {
        Flux<Person> personFlux = findAll();

        return personFlux.filter(person -> Objects.equals(person.getId(), id))
                .next();
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.just(raul, shrek, hulk, alexander);
    }
}
