package com.elastic.search;

import com.alibaba.fastjson.JSON;
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
import java.util.List;


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

	@Test
	public void queryESById() throws IOException {
		String id = "\"BbHpSnIBSuUbzMFMyzl7\"";
		Person person = personService.queryESById("1");
		log.info("根据id 查询es:{}", JSON.toJSONString(person));
	}

	@Test
	public void queryFetchSource() throws IOException {
		List<Person> personList = personService.queryFetchSource();
		log.info("查询全部数据，且过滤想要的字段：{}",JSON.toJSONString(personList));
	}

	@Test
	public void queryByPage() throws IOException {
		List<Person> personList = personService.queryByPage();
		log.info("分页查询ES：{}",JSON.toJSONString(personList));
	}

	@Test
	public void termQuery() throws IOException {
		Person person = new Person();
		person.setUsername("一点点");
		List<Person> personList = personService.termQuery(person);
		log.info("根据搜索字段匹配查询ES：{}",JSON.toJSONString(personList));
	}

}
