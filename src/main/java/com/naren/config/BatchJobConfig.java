package com.naren.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.naren.processor.Processor;
import com.naren.user.repo.User;
import com.naren.writer.DBWriter;

@Configuration
public class BatchJobConfig {

//	@Autowired
//	public JobBuilderFactory jobBuilderFactory;
//	@Autowired
//	public StepBuilderFactory stepBuilderFactory;
//
//	@Autowired
//	public ItemReader<User> itemReader;
	
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	public ItemWriter<User> itemWriter;
	
	@Autowired
	JobRepository jobRepository;

	@Bean
	public Job csvBatchJob(JobRepository jobRepository) {
		return new JobBuilder("csvBatchJob",jobRepository).incrementer(new RunIdIncrementer()).start(step(jobRepository))
				.build();
	}
	@Bean
	public Step step(JobRepository jobRepository) {
		return new StepBuilder("step",jobRepository)
		.<User, User>chunk(3,this.transactionManager).reader(itemReader())
				.processor(itemProcessor()).writer(itemWriter()).faultTolerant()
				.retry(Exception.class)
				.retryLimit(3).build();
	}

	
	@Bean
	public Processor itemProcessor() {
		return new Processor();
	}
	@Bean
	public DBWriter itemWriter() {
		return new DBWriter();
	}
	@Bean
	public FlatFileItemReader<User> itemReader() {

		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new FileSystemResource("src/main/resources/users.csv"));
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}

	@Bean
	public LineMapper<User> lineMapper() {

		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("id", "name", "dept", "salary");

		BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(User.class);

		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);

		return defaultLineMapper;
	}

}
