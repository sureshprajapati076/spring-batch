package com.example.batch.utils;

import com.example.batch.domain.User;
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

    @Value("${query:null}")
    String query;

    @Override
    public void afterJob(JobExecution jobExecution){
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            jdbcTemplate.query(query,
                    (rs,row)-> User.builder()
                            .fname(rs.getString("FNAME"))
                            .build()
            ).stream().map(User::getFname).forEach(System.out::println);
        }
    }

}
