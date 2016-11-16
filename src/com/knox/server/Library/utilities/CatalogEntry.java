package com.knox.server.Library.utilities;

import com.knox.server.Library.except.InvalidValueException;
import com.sun.istack.internal.NotNull;
import org.bson.Document;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.json.simple.DeserializationException;

import java.time.format.DateTimeParseException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CatalogEntry {

    private String title;
    private String author;
    private Owner owner;
    private String description = null;
    private String location = null;
    private Date ownedSince = null;

    public CatalogEntry(@NotNull String title,
                        @NotNull String author,
                        @NotNull Owner owner,
                        String description,
                        String location,
                        Date ownedSince) {

        this.title = title;
        this.author = author;
        this.owner = owner;

        if (description != null && !description.trim().isEmpty())
            this.description = description;
        if (location != null && !location.trim().isEmpty())
            this.location = location;
        if (ownedSince != null)
            this.ownedSince = ownedSince;
    }

    public CatalogEntry(String json) throws DeserializationException, InvalidValueException, ClassCastException {
        JsonObject catalogJson = (JsonObject) Jsoner.deserialize(json);
        this.initialize(catalogJson);
    }

    public CatalogEntry(JsonObject obj) throws InvalidValueException, ClassCastException {
        this.initialize(obj);
    }

    public CatalogEntry(Document doc) throws InvalidValueException, ClassCastException {
        this.initialize(doc);
    }

    public String stringify() {
        JsonObject resultObj = new JsonObject();
        resultObj.put("title", this.title);
        resultObj.put("author", this.author);

        JsonObject ownerObj = new JsonObject();
        ownerObj.put("firstName", this.owner.getFirstName());
        ownerObj.put("lastName", this.owner.getLastName());
        resultObj.put("owner", ownerObj);

        resultObj.put("description", this.description);
        resultObj.put("location", this.location);
        resultObj.put("ownedSince", this.ownedSince instanceof Date ? this.ownedSince : null);

        return resultObj.toJson();
    }

    public Document toDocument() {
        HashMap<String, Object> map = new HashMap();
        map.put("title", this.title);
        map.put("author", this.author);
        HashMap<String, String> ownerMap = new HashMap();
        ownerMap.put("firstName", this.owner.getFirstName());
        ownerMap.put("lastName", this.owner.getLastName());
        map.put("owner", ownerMap);
        map.put("description", this.description);
        map.put("location", this.location);
        map.put("ownedSince", this.ownedSince);
        return new Document(map);
    }

    private void initialize(Map entry) throws DateTimeParseException {
        if (entry == null)
            throw new InvalidValueException("null object passed in.");
        // Required fields
        if (!entry.containsKey("title") || !entry.containsKey("author") || !entry.containsKey("owner"))
            throw new InvalidValueException("missing required key");
        this.title = (String) entry.get("title");
        this.author = (String) entry.get("author");
        Map ownerObj = (Map) entry.get("owner");
        if (ownerObj.get("firstName") == null || ownerObj.get("firstName") == null)
            throw new InvalidValueException("owner object missing required key.");
        this.owner = new Owner((String) ownerObj.get("firstName"), (String) ownerObj.get("lastName"));

        // Optional fields
        this.description = (String) entry.get("description");
        this.location = (String) entry.get("location");
        if (entry.get("ownedSince") != null)
            this.ownedSince = DateFormat.parse((String) entry.get("ownedSince"));
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Date getOwnedSince() {
        return ownedSince;
    }
    public void setOwnedSince(Date ownedSince) {
        this.ownedSince = ownedSince;
    }
}