package com.qhjf.cfm.web.utils.comm.file.tool;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.qhjf.cfm.exceptions.AttachmentException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.config.MongoConfigSection;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import com.qhjf.cfm.web.utils.comm.file.info.MongoFileInfo;
import org.bson.types.ObjectId;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MongoDB文件保存、读取工具类
 *
 */
public class MongoFileTransTool implements FileTransTool {
	private final static int DEFAULT_BUFFER_SIZE = 8196;
	private final static int DEFAULT_PORT = 27017;
	private final static String DEFAULT_DB_NAME = "local";

	private static MongoFileTransTool instance = null;

	private MongoClientOptions.Builder build;
	private ServerAddress serverAddress;
	private MongoClientOptions options;
	private GridFSBucket gridFSBucket;
	private MongoDatabase database;
	private MongoClient client;
	private String dbName;

	public static MongoFileTransTool getInstance(MongoConfigSection childConfig){
		if(instance == null ){
			String host = childConfig.getIp();
			int port = childConfig.getPort() > 0 ?childConfig.getPort() : DEFAULT_PORT;
			String db = childConfig.getDbName();
			
			instance = new MongoFileTransTool(host, port,db);
		}
		return instance;
	}


	private MongoFileTransTool(String host, int port, String db) {
		serverAddress = new ServerAddress(host, port);
		build = new MongoClientOptions.Builder();
		//与数据最大连接数50  
		build.connectionsPerHost(50);
		//如果当前所有的connection都在使用中，则每个connection上可以有50个线程排队等待  
		build.threadsAllowedToBlockForConnectionMultiplier(50);
		build.connectTimeout(1*60*1000);
		build.maxWaitTime(2*60*1000);
		options = build.build();
		if (db != null && !"".equals(db)) {
			this.dbName = db;
		}else{
			this.dbName = DEFAULT_DB_NAME;
		}
	}


	/**
	 * 添加一个新文件到GridFS
	 * 保存成功返回objectId，用于下载新增的文件
	 * */
	@Override
	public String addNewFileByStream(String filename, InputStream source) {
		ObjectId objectId;
		try {
			this.init();
			objectId = gridFSBucket.uploadFromStream(filename, source);
		} finally {
			this.close();
		}
		return (objectId == null) ? "" : objectId.toString();
	}

	/**
	 * 添加一个新文件到GridFS
	 * @param filename  原始文件名
	 * @param source    文件数据
	 * @return
	 */
	@Override
	public String addNewFileByArray(String filename,byte[] source) {
		ObjectId objectId;
		try {
			this.init();
			ByteArrayInputStream sourceStream = new ByteArrayInputStream(source);
			objectId = gridFSBucket.uploadFromStream(filename, sourceStream);
		} finally {
			this.close();
		}
		return (objectId == null) ? "" : objectId.toString();
	}

	/**
	 * addNewFileToGridFS的重载方法,只要求数据的byte数组
	 * 会自动计算字节数组的md5值来作为文件名
	 * */
	@Override
	public String addNewFileByArray(byte[] source) {
		ObjectId objectId;
		try {
			this.init();
			String filename = String.format("%s.bin", MD5Kit.byteArrayToMD5(source));
			ByteArrayInputStream sourceStream = new ByteArrayInputStream(source);
			objectId = gridFSBucket.uploadFromStream(filename, sourceStream);
		} finally {
			this.close();
		}
		return (objectId == null) ? "" : objectId.toString();
	}

	/**
	 * 基于objectId删除已有的文件
	 * */
	@Override
	public void deleteFileByObjectId(String objectIdValue) {
		try {
			this.init();
			ObjectId objectId = new ObjectId(objectIdValue);
			gridFSBucket.delete(objectId);
		} finally {
			this.close();
		}
	}

	/**
	 * 基于objectId获取文件的内容
	 * */
	@Override
	public FileInfo getFileByObjectid(String objectId) throws BusinessException {
		MongoFileInfo result = new MongoFileInfo();
		try {
			this.init();
			try {
				GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(new ObjectId(objectId));
				buildMongoFileInfo(result, gridFSDownloadStream);
			} catch (Exception e) {
				throw new AttachmentException("获取文件类型失败，文件不存在或已损坏！");
			} 
		} finally {
			this.close();
		}
		return result;
	}
	@Override
	public FileInfo getFileByFileName(String fileName) throws BusinessException{
		MongoFileInfo result = new MongoFileInfo();
		try {
			this.init();
			try {
				GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(fileName);
				buildMongoFileInfo(result, gridFSDownloadStream);
			} catch (Exception e) {
				throw new AttachmentException("获取文件类型失败，文件不存在或已损坏！");
			} 
		} finally {
			this.close();
		}
		return result;
	}

	/**
	 * GridFSDownloadStream对象转MongoFileInfo对象
	 * @param info
	 * @param stream
	 */
	private void buildMongoFileInfo(MongoFileInfo info, GridFSDownloadStream stream){

		int counter = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		ByteArrayOutputStream targetStream = new ByteArrayOutputStream();

		info.setFilename(stream.getGridFSFile().getFilename());
		info.setSignContent(stream.getGridFSFile().getMD5());
		info.setSize(stream.getGridFSFile().getLength());

		try {
			while((counter=stream.read(buffer))!=-1) {
				if(counter<DEFAULT_BUFFER_SIZE)
					targetStream.write(buffer, 0, counter);
				else
					targetStream.write(buffer);
			}
			info.setContent(targetStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(stream != null){
				stream.close();
			}

		}
	}


	private void init() {
		this.client = new MongoClient(this.serverAddress, this.options);
		this.database = client.getDatabase(this.dbName);
		this.gridFSBucket = GridFSBuckets.create(this.database);
	}

	private void close() {
		if (null != this.client) {
			this.client.close();
		}
	}

}
