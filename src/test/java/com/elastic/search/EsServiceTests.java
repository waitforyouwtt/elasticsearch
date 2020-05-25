package com.elastic.search;

import com.elastic.search.entity.Person;
import com.elastic.search.service.PersonService;
import com.elastic.search.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;


@Component
@Slf4j
class EsServiceTests extends ElasticsearchApplicationTests{

	@Autowired
	PersonService personService;

	@Test
	public void createIndexTest(){
		personService.createIndex();
	}

	@Test
	public void addDocTest() throws IOException {
		Person person = new Person();
		person.setId(1);
		person.setUsername("一点点");
		person.setAge(21);
		person.setBirthday(DateUtil.stringToDate("2020-05-25 00:00:00"));
		person.setDeposit(new BigDecimal("55.5"));
		person.setAddress("河南沈丘");
		person.setStatus(0);
		person.setCreateTime(new Date());
		person.setUpdateTime(new Date());
		personService.addDoc(person);
	}

	@Test
	public void updateDocTest() throws IOException {
		Person person = new Person();
		person.setId(1);
		person.setUsername("凤凰小哥哥a");
		person.setAge(22);
		person.setBirthday(DateUtil.stringToDate("2020-05-26 00:00:00"));
		person.setDeposit(new BigDecimal("55.5"));
		person.setAddress("河南沈丘");
		person.setStatus(0);
		person.setUpdateTime(new Date());
		personService.updateDoc(person);
	}

}
