package com.example.springbatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			Date start = jobExecution.getCreateTime();

			//  get job's end time
			Date end = jobExecution.getEndTime();

			// get diff between end time and start time
			long diff = end.getTime() - start.getTime();
			log.info("!!! JOB FINISHED! Time to verify the results");

			List<Person> personList=jdbcTemplate.query("SELECT first_name, last_name FROM people",
				(rs, row) -> new Person(
					rs.getString(1),
					rs.getString(2))
			).stream().collect(Collectors.toList());

			ObjectMapper mapper = new ObjectMapper();

			try {
				System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(personList));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
				System.out.println("TIME: "+diff);
		}

	}
}
