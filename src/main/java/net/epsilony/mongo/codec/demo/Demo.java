package net.epsilony.mongo.codec.demo;

import java.io.IOException;
import java.util.ArrayList;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import freemarker.template.TemplateException;
import net.epsilony.mongo.codec.MetaCodecProvider;

public class Demo {

	public static void main(String[] args) throws ClassNotFoundException, IOException, IllegalAccessException,
			InstantiationException, TemplateException {

		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("testmeta");

		MetaCodecProvider metaCodecProvider = new MetaCodecProvider();
		metaCodecProvider.setPackageName("net.epsilony.mongo.sample");

		CodecRegistry codecRegistry = database.getCodecRegistry();
		codecRegistry = CodecRegistries.fromRegistries(codecRegistry, CodecRegistries.fromProviders(metaCodecProvider));
		database = database.withCodecRegistry(codecRegistry);
		MongoCollection<SampleBean> collection = database.getCollection("t", SampleBean.class);
		collection.drop();
		SampleBean in = new SampleBean();
		collection.insertOne(in);

		ArrayList<SampleBean> out = collection.find().into(new ArrayList<SampleBean>());
		System.out.println(in.equals(out.get(0)));

		mongoClient.close();

	}
}
