/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.data.mongo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@SpringBootApplication
public class SampleMongoApplication implements CommandLineRunner {

	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	

	@Override
	public void run(String... args) throws Exception {
		
		System.out.println("gridFsTemplate=>"+gridFsTemplate);
		
		//Query query = new Query();
		//gridFsTemplate.delete(query); // del all
		
		InputStream inputStream = new FileInputStream("D:\\xml\\productsDump.xml"); 
		DBObject metaData = new BasicDBObject();
		metaData.put("user", "alex");
		String id = gridFsTemplate.store(inputStream, "productsDump.xml", "text/xml", metaData).getId().toString();
		System.out.println("id=>"+id);
		
		Query q1 = new Query(Criteria.where("_id").is(id));
		GridFSDBFile gridFsdbFile = gridFsTemplate.findOne(q1);
		String type1 = gridFsdbFile.getContentType();
		System.out.println("type=>"+type1);
		
		Query q2 = new Query(Criteria.where("metadata.user").is(new String("alex")));
		List<GridFSDBFile> gridFsdbFiles = gridFsTemplate.find(q2);
		for (GridFSDBFile g : gridFsdbFiles) {
			String type2 = g.getContentType();
			System.out.println("type=>"+type2);
		}
		
		
		//Query q3 = new Query(Criteria.where("_id").is(id));
		//gridFsTemplate.delete(q3);
		
		this.repository.deleteAll();

		// save a couple of customers
		this.repository.save(new Customer("Alice", "Smith"));
		this.repository.save(new Customer("Bob", "Smith"));

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : this.repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		// fetch an individual customer
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(this.repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : this.repository.findByLastName("Smith")) {
			System.out.println(customer);
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleMongoApplication.class, args);
	}

}
