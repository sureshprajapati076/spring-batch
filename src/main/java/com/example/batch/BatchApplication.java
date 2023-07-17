package com.example.batch;

import com.example.batch.domain.Person;
import com.example.batch.domain.PersonDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class BatchApplication {


	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Value("${query:null}")
	String jdbcQuery;


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

	@Autowired
	private DataSource dataSource;

	@Bean
	@StepScope
	public JdbcPagingItemReader<PersonDto> readNow(){
		final JdbcPagingItemReader<PersonDto> reader = new JdbcPagingItemReader<>();
		final PersonMapper personMapper = new PersonMapper();
		reader.setDataSource(dataSource);
		reader.setFetchSize(10);
		reader.setPageSize(10);
		reader.setRowMapper(personMapper);
		reader.setQueryProvider(createQuery());

		return reader;

	}

	private MySqlPagingQueryProvider createQuery() {
		final Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("id",Order.ASCENDING);
		final MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("*");
		queryProvider.setFromClause(getFromClause());
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

	private String getFromClause() {
		return "( "+ jdbcQuery + ")" + " RESULT_TABLE ";
	}


	@Bean
	@StepScope
	Tasklet tasklet(@Value("#{jobParameters['uuid']}") String uuid){
		return ((contribution, chunkContext) -> {
			System.out.println("HELLO WORLD! " + uuid);
			return RepeatStatus.FINISHED;
		});
	}

	@Bean
	JdbcBatchItemWriter<Person> writer(){
		return new JdbcBatchItemWriterBuilder<Person>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("update my_table set name=:name where id=:id")
				.dataSource(dataSource)
				.build();

	}

	@Bean
	Step csvToDb (JobRepository repository, JdbcBatchItemWriter<Person> writer, PlatformTransactionManager tmx){

		return new StepBuilder("csvToDb",repository)
				.<PersonDto,Person> chunk(2,tmx)

				.reader(readNow())
				.processor(processor())
				.writer(writer())
				.taskExecutor(taskExecutor())
				.build();

	}

	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	private TaskExecutor taskExecutor() {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setThreadNamePrefix("BATCH -");
		taskExecutor.setConcurrencyLimit(10);
		return taskExecutor;
	}

	@Bean
	Job job(JobCompletionNotificaitonListner listner, JobRepository jobRepository, Step csvToDb){
		return new JobBuilder("job",jobRepository)
				.incrementer(new RunIdIncrementer())
				.listener(listner)
				.start(csvToDb)
				.build();
	}

	@Bean
	JdbcTemplate jdbcTemplate(DataSource dataSource){
		return new JdbcTemplate(dataSource);
	}



//	@Bean
//	Step step1(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager platformTransactionManager){
//		return new StepBuilder("step1",jobRepository)
//				.tasklet(tasklet,platformTransactionManager)
//				.build();
//	}

}