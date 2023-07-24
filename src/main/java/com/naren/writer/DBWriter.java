package com.naren.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.naren.user.repo.User;
import com.naren.user.repo.UserRepository;

@Component
public class DBWriter implements ItemWriter<User> {
	
	@Autowired
	private UserRepository userRepository;


	@Override
	public void write(Chunk<? extends User> chunk) throws Exception {
		System.out.println("Data Saved for Users: " + chunk);
		userRepository.saveAll(chunk);

	}
}