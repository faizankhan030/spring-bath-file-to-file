package com.cognizant.demo.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.cognizant.demo.model.UserModel;

@Component
public class Processor implements ItemProcessor<UserModel, UserModel> {

	@Override
	public UserModel process(UserModel item) throws Exception {
		System.out.println("==========In Processor======================="+item);
		// TODO Auto-generated method stub
		return item;
	}

}
