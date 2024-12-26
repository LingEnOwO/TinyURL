package student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository){
        return args -> {
            Student caleb = new Student(
                    "Caleb",
                    "calebSu@gmail.com",
                    LocalDate.of(2001, Month.JANUARY, 10)
            );
            Student alex = new Student(
                    "Alex",
                    "alex@gmail.com",
                    LocalDate.of(2003, Month.JANUARY, 10)
            );

            repository.saveAll(List.of(caleb, alex));
        };
    }
}
