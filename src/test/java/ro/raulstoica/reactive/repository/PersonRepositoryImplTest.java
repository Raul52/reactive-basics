package ro.raulstoica.reactive.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ro.raulstoica.reactive.domain.Person;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepository;

    @BeforeEach
    void setUp() {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        Person person = personMono.block();

        assertNotNull(person);
        assertEquals(person.getId(), 1);
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(person -> {
            assertNotNull(person);
            assertEquals(person.getId(), 1);
        });
    }

    @Test
    void getByIdSubscribeNotFoundId() {
        Mono<Person> personMono = personRepository.getById(-1);

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        personMono.subscribe(person -> {
            assertNotNull(person);
            assertNull(person.getId());
        });
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.map(person -> {
            System.out.println("Before map: " + person);

            return person.getFirstName();
        })
                .subscribe(person ->
                        System.out.println("After map: " + person));
    }

    @Test
    void fluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        Person person = personFlux.blockFirst();

        System.out.println(person);
    }

    @Test
    void fluxSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        personFlux.subscribe(System.out::println);
    }

    @Test
    void fluxToListMono() {
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> list.forEach(System.out::println));
    }

    @Test
    void testFindPersonById() {
        final Integer id = 3;
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        Mono<Person> personMono = personFlux.filter(person -> Objects.equals(person.getId(), id))
                .next();

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();

        personMono.subscribe(System.out::println);
    }

    @Test
    void testFindPersonByIdNotFound() {
        final Integer id = -1;
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        Mono<Person> personMono = personFlux.filter(person -> Objects.equals(person.getId(), id))
                .next();

        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();

        personMono.subscribe(System.out::println);
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {
        final Integer id = -1;
        Flux<Person> personFlux = personRepository.findAll();

        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();

        Mono<Person> personMono = personFlux.filter(person -> Objects.equals(person.getId(), id))
                .single();

        StepVerifier.create(personMono).expectNextCount(0).verifyError();

        //you can beautifully decide what to write on error and what to return when the error is detected
        personMono.doOnError(throwable -> System.out.println("Goes bazooka!"))
                .onErrorReturn(Person.builder().build())
                .subscribe(System.out::println);
    }
}