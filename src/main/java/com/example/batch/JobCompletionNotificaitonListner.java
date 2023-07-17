package com.example.batch;

import com.example.batch.domain.PersonDto;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificaitonListner implements JobExecutionListener {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${query}")
    String query;

    @Override
    public void afterJob(JobExecution jobExecution){
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            jdbcTemplate.query(query,
                    (rs,row)->
                new PersonDto(rs.getInt("id"), rs.getString("name"))
                    ).forEach(System.out::println);
        }
    }

}
