package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class BatchApplication {


	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}




	//below is for custom runner to kick batch job
	// need property  spring.batch.job.enabled= false
	@Bean
	ApplicationRunner runner(JobLauncher jobLauncher, Job job){
		return args -> {
			var jobParameters = new JobParametersBuilder()
					.addString("uuid",UUID.randomUUID().toString())
//					.addLocalDate("date", LocalDate.now())
					.toJobParameters();
			var run = jobLauncher.run(job,jobParameters);
			var instanceId = run.getJobInstance().getInstanceId();
			System.out.println("instance id"+instanceId);

		};
	}
}