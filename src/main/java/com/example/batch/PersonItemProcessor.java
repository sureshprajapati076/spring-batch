package com.example.batch;

import com.example.batch.domain.Person;
import com.example.batch.domain.PersonDto;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<PersonDto, Person> {

    @Override
    public Person process(PersonDto item) throws Exception {
        System.out.println(item);
        if(item.getName().toLowerCase().equals(item.getName())){
            item.setNote("Name Lower case Found");
            return new Person(item.getId(),item.getName().toUpperCase());
        }
        return new Person(item.getId(),item.getName());
    }
}
