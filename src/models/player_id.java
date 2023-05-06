package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
@JsonIgnoreProperties(ignoreUnknown = true)
public class player_id {
    public List<Uid> db;

}


