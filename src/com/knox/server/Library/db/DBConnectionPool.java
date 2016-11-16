package com.knox.server.Library.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

class DBConnectionPool {
    private static final int MAX_CONNECTIONS = Integer.valueOf(System.getenv("MONGO_CXS"));
    private static final String HOST = System.getenv("DB_HOST");
    private static final int PORT = Integer.valueOf(System.getenv("DB_PORT"));
    private static final MongoCredential CRED = MongoCredential.createScramSha1Credential("librarian", "Library", "iamalibrary".toCharArray());

    private static List<MongoCredential> credentials = new Vector<>();
    private static DBConnectionPool singleton = null;
    private static LinkedBlockingQueue<MongoClient> pool = new LinkedBlockingQueue<MongoClient>(MAX_CONNECTIONS);

    private DBConnectionPool() {
        if (credentials.size() == 0) credentials.add(CRED);
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            pool.add(new MongoClient(new ServerAddress(HOST, PORT), credentials));
        }
    }

    public static MongoClient getConnection() {
        MongoClient returnVal;

        if (singleton == null || singleton.getSize() == 0)
            singleton = new DBConnectionPool();

        try {
            do returnVal = pool.poll(50L, TimeUnit.MILLISECONDS); while (returnVal == null);
            return returnVal;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static void returnConnection(MongoClient conn) {
        pool.add(conn);
    }
    private static int getSize() { return pool.size(); }
}