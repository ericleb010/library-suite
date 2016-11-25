package com.knox.server.Library.utilities;

import com.knox.server.Library.db.DBUtils;
import com.knox.server.Library.except.InvalidValueException;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.DeserializationException;
import static org.junit.Assert.*;

import org.json.simple.Jsoner;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

public class CatalogEntryTest {
    protected CatalogEntry obj = null;

    @Before public void setUp() {
        this.obj = new CatalogEntry("NameOfBook", "NameOfAuthor", new Owner("FirstName", "LastName"), null, null, null);
    }

    @Test public void mongoInitializesProperly() {
        MongoClient conn = DBUtils.connect();
        DBUtils.disconnect(conn);
    }

    @Test public void constructorRunsUsingPlainStringsAndNulls() {
        assertNotNull(this.obj);
        assertEquals(this.obj.getTitle(), "NameOfBook");
        assertEquals(this.obj.getAuthor(), "NameOfAuthor");
        assertEquals(this.obj.getOwner(), new Owner("FirstName", "LastName"));
        assertNull(this.obj.getDescription());
        assertNull(this.obj.getLocation());
        assertNull(this.obj.getOwnedSince());
    }

    @Test public void constructorRunsUsingNonNullsForAllFields() {
        this.obj = new CatalogEntry("NameOfBook", "NameOfAuthor", new Owner("FirstName", "LastName"), "Description",
                "Location", LocalDate.parse("2009-10-03"));
        assertNotNull(this.obj);
        assertEquals(this.obj.getDescription(), "Description");
        assertEquals(this.obj.getLocation(), "Location");
        assertEquals(this.obj.getOwnedSince(), LocalDate.parse("2009-10-03"));
    }

    @Test public void constructorRunsUsingValidJsonString() {
        String json =
                "{\"title\":\"NameOfBook\",\"author\":\"NameOfAuthor\",\"owner\":{\"firstName\":\"FirstName\"," +
                        "\"lastName\":\"LastName\"},\"description\":\"Description\",\"location\":\"Location\"," +
                        "\"ownedSince\":\"2009-10-03\"}";

        try {
            this.obj = new CatalogEntry(json);
        } catch (Exception e) {
            fail("JSON parsing failed.");
        }
        assertNotNull(this.obj);
        assertEquals(this.obj.getTitle(), "NameOfBook");
        assertEquals(this.obj.getAuthor(), "NameOfAuthor");
        assertEquals(this.obj.getOwner(), new Owner("FirstName", "LastName"));
        assertEquals(this.obj.getDescription(), "Description");
        assertEquals(this.obj.getLocation(), "Location");
        assertEquals(this.obj.getOwnedSince(), LocalDate.parse("2009-10-03"));
    }

    @Test (expected = DeserializationException.class)
    public void constructorThrowsExceptionWhenJsonIsInvalid() throws DeserializationException {
        String json =
                "{\"title\":\"NameOfBook,\"author\":\"NameOfAuthor\",\"owner\":{\"firstName\":\"FirstName\"," +
                        "\"lastName\":\"LastName\"}}";
        this.obj = new CatalogEntry(json);
    }

    @Test (expected = ClassCastException.class)
    public void constructorThrowsExceptionWhenJsonTypesDontMatch() {
        String json =
                "{\"title\":{},\"author\":\"NameOfAuthor\",\"owner\":{\"firstName\":\"FirstName\"," +
                        "\"lastName\":\"LastName\"}}";
        try {
            this.obj = new CatalogEntry(json);
        } catch (DeserializationException e) {
            fail("JSON parsing failed.");
        }
    }

    @Test (expected = InvalidValueException.class)
    public void constructorThrowsExceptionWhenRequiredFieldIsMissing() {
        String json = "{\"title\":\"NameOfBook\",\"owner\":{\"firstName\":\"FirstName\",\"lastName\":\"LastName\"}}";
        try {
            this.obj = new CatalogEntry(json);
        } catch (DeserializationException e) {
            fail("JSON parsing failed.");
        }
    }

    @Test public void constructorRunsWhenOptionalFieldIsMissing() {
        String json =
                "{\"title\":\"NameOfBook\",\"author\":\"NameOfAuthor\",\"owner\":{\"firstName\":\"FirstName\"," +
                        "\"lastName\":\"LastName\"}}";
        try {
            this.obj = new CatalogEntry(json);
        } catch (DeserializationException e) {
            fail("JSON parsing failed.");
        }
    }

    @Test public void constructorRunsWhenUnknownFieldExists() {
        String json =
                "{\"title\":\"NameOfBook\",\"author\":\"NameOfAuthor\",\"owner\":{\"firstName\":\"FirstName\"," +
                        "\"lastName\":\"LastName\"},\"ownedSincee\":\"2009-10-03\"}";
        try {
            this.obj = new CatalogEntry(json);
        } catch (DeserializationException e) {
            fail("JSON parsing failed.");
        }
        assertNotNull(this.obj);
        assertNull(this.obj.getLocation());
        assertNull(this.obj.getDescription());
        assertNull(this.obj.getOwnedSince());
    }

    @Test public void constructorRunsWhenDocumentIsValid() {
        MongoClient conn = DBUtils.connect();
        MongoDatabase db = conn.getDatabase("Library");
        this.obj = new CatalogEntry(db.getCollection("Catalog").find().first());
        DBUtils.disconnect(conn);
    }

    @Test (expected = InvalidValueException.class)
    public void constructorThrowsExceptionWithNullDocument() {
        this.obj = new CatalogEntry(new Document());
    }



    // -----------------------------------



    @Test public void stringifyShouldHandleNulls() {
        String expected = "{\"owner\":{\"firstName\":\"FirstName\",\"lastName\":\"LastName\"},\"ownedSince\":null," +
                "\"author\":\"NameOfAuthor\",\"description\":null,\"location\":null,\"title\":\"NameOfBook\"}";
        assertEquals(expected, this.obj.stringify());
    }

    @Test public void stringifyShouldHandleDates() {
        String expected = "{\"owner\":{\"firstName\":\"FirstName\",\"lastName\":\"LastName\"},\"ownedSince\":\"2009-10-03\"," +
                "\"author\":\"NameOfAuthor\",\"description\":\"Description\",\"location\":\"Location\",\"title\":\"NameOfBook\"}";
        this.obj.setDescription("Description");
        this.obj.setLocation("Location");
        this.obj.setOwnedSince(LocalDate.parse("2009-10-03"));
        assertEquals(expected, this.obj.stringify());
    }

}