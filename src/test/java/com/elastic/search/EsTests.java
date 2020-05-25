package com.elastic.search;

import com.elastic.search.entity.Person;
import com.elastic.search.util.ElasticsearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
class EsTests extends ElasticsearchApplicationTests{

	@Autowired
	RestHighLevelClient client;


	//创建索引库
	@Test
	public void testCreateIndex() throws IOException {
		//创建索引对象
		CreateIndexRequest createIndexRequest = new CreateIndexRequest("studymodel");
		//设置参数
		createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
		//指定映射
		createIndexRequest.mapping("doc"," {\n" +
				" \t\"properties\": {\n" +
				"            \"studymodel\":{\n" +
				"             \"type\":\"keyword\"\n" +
				"           },\n" +
				"            \"name\":{\n" +
				"             \"type\":\"keyword\"\n" +
				"           },\n" +
				"           \"description\": {\n" +
				"              \"type\": \"text\",\n" +
				"              \"analyzer\":\"english\"\n" +
				"           },\n" +
				"           \"pic\":{\n" +
				"             \"type\":\"text\",\n" +
				"             \"index\":false\n" +
				"           }\n" +
				" \t}\n" +
				"}", XContentType.JSON);
		//操作索引的客户端
		IndicesClient indices = client.indices();
		//执行创建索引库
		CreateIndexResponse createIndexResponse = indices.create(createIndexRequest,RequestOptions.DEFAULT);
		//得到响应
		boolean acknowledged = createIndexResponse.isAcknowledged();
		System.out.println(acknowledged);
	}

	//添加文档
	@Test
	public void testAddDoc() throws IOException {
		//文档内容
		//准备json数据
		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("name", "spring cloud实战");
		jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
		jsonMap.put("studymodel", "201001");
		SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jsonMap.put("timestamp", dateFormat.format(new Date()));
		jsonMap.put("price", 5.6f);

		//创建索引创建对象
		IndexRequest indexRequest = new IndexRequest("elasticsearch_test","doc");
		//文档内容
		indexRequest.source(jsonMap);
		//通过client进行http的请求
		IndexResponse indexResponse = client.index(indexRequest,RequestOptions.DEFAULT);
		DocWriteResponse.Result result = indexResponse.getResult();
		System.out.println(result);
	}

	public void updateDoc() throws IOException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		Person person = new Person();
		person.setId(1L);
		person.setUsername("test");
		person.setAge (20);
		person.setStatus(0);
		person.setBirthday(LocalDate.parse("2020-01-01").format(formatter));
		person.setRemark("备注");
		person.setUpdateDate(LocalDateTime.now().format(formatter2));

		XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
		xContentBuilder = ElasticsearchUtils.objectToXContentBuilder(xContentBuilder, person);

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.doc(xContentBuilder);

		UpdateResponse update = client.update(updateRequest, RequestOptions.DEFAULT);
	}

	//查询文档
	@Test
	public void testGetDoc() throws IOException {
		//查询请求对象
		GetRequest getRequest = new GetRequest("elasticsearch_test","_doc","EGptNnIBywKalVIKEbCN");
		GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
		//得到文档的内容
		Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
		System.out.println(sourceAsMap);
	}

	//搜索全部记录
	@Test
	public void testSearchAll() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//搜索方式
		//matchAllQuery搜索全部
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//分页查询
	@Test
	public void testSearchPage() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//设置分页参数
		//页码
		int page = 1;
		//每页记录数
		int size = 1;
		//计算出记录起始下标
		int from  = (page-1)*size;
		searchSourceBuilder.from(from);//起始记录下标，从0开始
		searchSourceBuilder.size(size);//每页显示的记录数
		//搜索方式
		//matchAllQuery搜索全部
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//TermQuery
	@Test
	public void testTermQuery() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//设置分页参数
		//页码
		int page = 1;
		//每页记录数
		int size = 1;
		//计算出记录起始下标
		int from  = (page-1)*size;
		searchSourceBuilder.from(from);//起始记录下标，从0开始
		searchSourceBuilder.size(size);//每页显示的记录数
		//搜索方式
		//termQuery
		searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}


	//根据id查询
	@Test
	public void testTermQueryByIds() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//搜索方式
		//根据id查询
		//定义id
		String[] ids = new String[]{"1","2"};
		searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	@Test
	public void testMatchQuery() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//搜索方式
		//MatchQuery
		searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架")
				.minimumShouldMatch("80%"));
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//MultiMatchQuery
	@Test
	public void testMultiMatchQuery() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//搜索方式
		//MultiMatchQuery
		searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description")
				.minimumShouldMatch("50%")
				.field("name",10));
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}
	}

	//BoolQuery
	@Test
	public void testBoolQuery() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//boolQuery搜索方式
		//先定义一个MultiMatchQuery
		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
				.minimumShouldMatch("50%")
				.field("name", 10);
		//再定义一个termQuery
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

		//定义一个boolQuery
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(multiMatchQueryBuilder);
		boolQueryBuilder.must(termQueryBuilder);

		searchSourceBuilder.query(boolQueryBuilder);
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//filter
	@Test
	public void testFilter() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//boolQuery搜索方式
		//先定义一个MultiMatchQuery
		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring css", "name", "description")
				.minimumShouldMatch("50%")
				.field("name", 10);

		//定义一个boolQuery
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(multiMatchQueryBuilder);
		//定义过虑器
		boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
		boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(90).lte(100));

		searchSourceBuilder.query(boolQueryBuilder);
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//Sort
	@Test
	public void testSort() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//boolQuery搜索方式
		//定义一个boolQuery
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		//定义过虑器
		boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

		searchSourceBuilder.query(boolQueryBuilder);
		//添加排序
		searchSourceBuilder.sort("studymodel", SortOrder.DESC);
		searchSourceBuilder.sort("price", SortOrder.ASC);
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});
		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			String name = (String) sourceAsMap.get("name");
			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}

	}

	//Highlight
	@Test
	public void testHighlight() throws IOException, ParseException {
		//搜索请求对象
		SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
		//指定类型
		searchRequest.types("doc");
		//搜索源构建对象
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//boolQuery搜索方式
		//先定义一个MultiMatchQuery
		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发框架", "name", "description")
				.minimumShouldMatch("50%")
				.field("name", 10);

		//定义一个boolQuery
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(multiMatchQueryBuilder);
		//定义过虑器
		boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

		searchSourceBuilder.query(boolQueryBuilder);
		//设置源字段过虑,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
		searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

		//设置高亮
		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.preTags("<tag>");
		highlightBuilder.postTags("</tag>");
		highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
//        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
		searchSourceBuilder.highlighter(highlightBuilder);

		//向搜索请求对象中设置搜索源
		searchRequest.source(searchSourceBuilder);
		//执行搜索,向ES发起http请求
		SearchResponse searchResponse = client.search(searchRequest,RequestOptions.DEFAULT);
		//搜索结果
		SearchHits hits = searchResponse.getHits();
		//匹配到的总记录数
		TotalHits totalHits = hits.getTotalHits();
		//得到匹配度高的文档
		SearchHit[] searchHits = hits.getHits();
		//日期格式化对象
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(SearchHit hit:searchHits){
			//文档的主键
			String id = hit.getId();
			//源文档内容
			Map<String, Object> sourceAsMap = hit.getSourceAsMap();
			//源文档的name字段内容
			String name = (String) sourceAsMap.get("name");
			//取出高亮字段
			Map<String, HighlightField> highlightFields = hit.getHighlightFields();
			if(highlightFields!=null){
				//取出name高亮字段
				HighlightField nameHighlightField = highlightFields.get("name");
				if(nameHighlightField!=null){
					Text[] fragments = nameHighlightField.getFragments();
					StringBuffer stringBuffer = new StringBuffer();
					for(Text text:fragments){
						stringBuffer.append(text);
					}
					name = stringBuffer.toString();
				}
			}

			//由于前边设置了源文档字段过虑，这时description是取不到的
			String description = (String) sourceAsMap.get("description");
			//学习模式
			String studymodel = (String) sourceAsMap.get("studymodel");
			//价格
			Double price = (Double) sourceAsMap.get("price");
			//日期
			Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
			System.out.println(name);
			System.out.println(studymodel);
			System.out.println(description);
		}
	}


}
