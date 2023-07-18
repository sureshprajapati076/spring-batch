package com.example.batch.config;

import com.example.batch.utils.JobCompletionNotificaitonListner;
import com.example.batch.utils.UserItemProcessor;
import com.example.batch.utils.UserMapper;
import com.example.batch.domain.User;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchConfig {


    @Value("${query:null}")
    String jdbcQuery;

    @Bean
    @StepScope
    public JdbcPagingItemReader<User> readNow(DataSource dataSource){
        final JdbcPagingItemReader<User> reader = new JdbcPagingItemReader<>();
        final UserMapper userMapper = new UserMapper();
        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setPageSize(10);
        reader.setRowMapper(userMapper);
        reader.setQueryProvider(createQuery());

        return reader;

    }

    private MySqlPagingQueryProvider createQuery() {
        final Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("ID",Order.ASCENDING);
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
    JdbcBatchItemWriter<User> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE USERS SET COMMENTS= :comments WHERE ID=:id")
                .dataSource(dataSource)
                .build();

    }

    @Bean
    Step updateComment (DataSource dataSource, JobRepository repository, JdbcBatchItemWriter<User> writer, PlatformTransactionManager tmx){

        return new StepBuilder("commentUpdate",repository)
                .<User,User> chunk(100,tmx)
                .reader(readNow(dataSource))
                .processor(processor())
                .writer(writer(dataSource))
                .taskExecutor(taskExecutor())
                .build();

    }

    @Bean
    public UserItemProcessor processor() {
        return new UserItemProcessor();
    }

    private TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setThreadNamePrefix("BATCH -");
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }

    @Bean
    Job job(JobCompletionNotificaitonListner listner, JobRepository jobRepository, Step commentUpdate){
        return new JobBuilder("job",jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listner)
                .start(commentUpdate)
                .build();
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
