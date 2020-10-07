package com.cognizant.demo.config;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;

import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.cognizant.demo.model.UserModel;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration

public class BatchConfiguration {
	@Bean
	
	public Job job(JobBuilderFactory jobBuilderFactory
					,StepBuilderFactory stepBuilderFactory
					,ItemReader<UserModel> itemReader
					,ItemProcessor<UserModel, UserModel> itemProcessor
					,ItemWriter<UserModel> itemWriter) {
		
		
		  Step step = stepBuilderFactory.get("ETL-file-load")
	                .<UserModel, UserModel>chunk(100)
	                .reader(jsonItemReader())
	                .processor(itemProcessor)
	                .writer(itemWriter)
	                .build();


	        return jobBuilderFactory.get("ETL-Load")
	                .incrementer(new RunIdIncrementer())
	                .start(step)
	                .build();
	    }
	
	@Bean
	public JsonItemReader<UserModel> jsonItemReader() {
		System.out.println("i am herbe");

	   ObjectMapper objectMapper = new ObjectMapper();
	   // configure the objectMapper as required
	   JacksonJsonObjectReader<UserModel> jsonObjectReader = 
	            new JacksonJsonObjectReader<>(UserModel.class);
	   jsonObjectReader.setMapper(objectMapper);

	   return new JsonItemReaderBuilder<UserModel>()
	                 .jsonObjectReader(jsonObjectReader)
	                 .resource(new ClassPathResource("trades.json"))
	                 .name("tradeJsonItemReader")
	                 .build();
	}
	
	
//	 @Bean
//	    public FlatFileItemReader<UserModel> itemReader() {
//
//	        FlatFileItemReader<UserModel> flatFileItemReader = new FlatFileItemReader<>();
//	        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/users.csv"));
//	        flatFileItemReader.setName("CSV-Reader");
//	        flatFileItemReader.setLinesToSkip(1);
//	        flatFileItemReader.setLineMapper(lineMapper());
//	        return flatFileItemReader;
//	    }
//
//	    @Bean
//	    public LineMapper<UserModel> lineMapper() {
//
//	        DefaultLineMapper<UserModel> defaultLineMapper = new DefaultLineMapper<>();
//	        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
//
//	        lineTokenizer.setDelimiter(",");
//	        lineTokenizer.setStrict(false);
//	        lineTokenizer.setNames(new String[]{"id", "First_Name", "Last_Name", "email"});
//
//	        BeanWrapperFieldSetMapper<UserModel> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
//	        fieldSetMapper.setTargetType(UserModel.class);
//
//	        defaultLineMapper.setLineTokenizer(lineTokenizer);
//	        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
//
//	        return defaultLineMapper;
//	    }
	    
	    
	    
	    @Bean
	    public FlatFileItemWriter<UserModel> itemWriter() throws Exception {
	    	BeanWrapperFieldExtractor<UserModel> fieldExtractor = new BeanWrapperFieldExtractor<>();
	    	fieldExtractor.setNames(new String[] {"id", "firstName", "lastName", "email"});
	    	fieldExtractor.afterPropertiesSet();

	    	FormatterLineAggregator<UserModel> lineAggregator = new FormatterLineAggregator<>();
	    	lineAggregator.setFormat("%-8.8s%-10.10s%-10.10s%-25.25s"); //-8.8s%-12.12s%-11.11s%-16.16s
	    	lineAggregator.setFieldExtractor(fieldExtractor);

	    	return new FlatFileItemWriterBuilder<UserModel>()
	    				.name("customerCreditWriter")
	    				.resource(new ClassPathResource("usersnew.txt") )
	    				.lineAggregator(lineAggregator)
	    				.build();
	    }

}
