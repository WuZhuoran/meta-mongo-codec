package net.epsilony.mongo.codec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import freemarker.template.TemplateException;

public class Temp {

	public StringBuffer getBuffer() {
		return null;
	}

	public List<ObjectId> getIds() {
		return null;
	}

	public void setIds(List<ObjectId> ids) {

	}

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

	public ObjectId getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setId(ObjectId object) {
		// TODO Auto-generated method stub

	}

	public void setIntV(int v) {

	}

	public int getIntV() {
		return 0;
	}
}
