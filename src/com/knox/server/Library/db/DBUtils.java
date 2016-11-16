package com.knox.server.Library.db;

import com.mongodb.MongoClient;

public class DBUtils {

    public static MongoClient connect() {
        return DBConnectionPool.getConnection();
    }
    public static void disconnect(MongoClient conn) { DBConnectionPool.returnConnection(conn); }

}