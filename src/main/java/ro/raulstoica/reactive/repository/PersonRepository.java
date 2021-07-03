package ro.raulstoica.reactive.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ro.raulstoica.reactive.domain.Person;


public interface PersonRepository  {

    Mono<Person> getById(Integer id);

    Flux<Person> findAll();
}
