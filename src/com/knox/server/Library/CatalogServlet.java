package com.knox.server.Library;

import com.knox.server.Library.db.DBUtils;
import com.knox.server.Library.utilities.CatalogEntry;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.DeserializationException;
import org.json.simple.JsonObject;
import org.json.simple.JsonArray;
import org.json.simple.Jsoner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "CatalogServlet", value = "/Catalog")
public class CatalogServlet extends HttpServlet {


    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getContentType().equals("application/json")) {
            try {
                Object fromJson = null;
                fromJson = Jsoner.deserialize(req.getReader());

                MongoClient conn = DBUtils.connect();
                MongoDatabase db = conn.getDatabase("Library");
                if (fromJson instanceof JsonObject) {
                    db.getCollection("Catalog").insertOne((new CatalogEntry((JsonObject) fromJson)).toDocument());
                }
                else if (fromJson instanceof JsonArray) {
                    ArrayList<Document> entries = new ArrayList();
                    ((JsonArray) fromJson).forEach(
                            entry -> entries.add((new CatalogEntry((JsonObject) entry)).toDocument()));
                    db.getCollection("Catalog").insertMany(entries);
                }
                else
                    res.sendError(400, "Expected either an array or an object.");
            } catch (DeserializationException e) {
                res.sendError(400, "Invalid JSON.");
                return;
            } catch (ClassCastException e) {
                res.sendError(400, "Invalid type or hierarchy provided.");
            }
        }
        else res.setStatus(400);
    }


    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        JsonArray result = new JsonArray();
        MongoClient conn = DBUtils.connect();
        MongoDatabase db = conn.getDatabase("Library");
        db.getCollection("Catalog").find().forEach(new Block<Document>() {
            public void apply(Document entry) {
                try {
                    result.add(Jsoner.deserialize((new CatalogEntry(entry)).stringify()));
                } catch(DeserializationException e) {};
            }
        });
        DBUtils.disconnect(conn);
        res.setContentType("application/json");
        res.getWriter().write(Jsoner.serialize(result) + "\n");
        res.getWriter().flush();
    }
}
