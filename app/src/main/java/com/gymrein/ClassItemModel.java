package com.gymrein;

/**
 * Created by jcisneros77 on 4/10/17.
 */

public class ClassItemModel {
    private String id;
    private boolean assisted;
    private String event_id;
    private String instructor_id;
    private String location_id;
    private String date;
    private String room;
    private String duration;
    private String finish;
    private String limit;
    private boolean available;
    private String logo_url;
    private String event_name;
    private String event_description;
    private String location_name;
    private String location_adress;

    public ClassItemModel(String Id,Boolean Assisted, String Event_id, String Ins_id, String Loc_id,String Date_id,String Room,String Duration,String Finish, String Limit, Boolean Available,
                            String Logo_Url, String Event_name, String Event_desc, String Location_name, String Location_address){
        id = Id;
        assisted = Assisted;
        event_id = Event_id;
        event_name = Event_name;
        event_description = Event_desc;
        instructor_id = Ins_id;
        location_id = Loc_id;
        location_name = Location_name;
        location_adress = Location_address;
        date = Date_id;
        room = Room;
        duration = Duration;
        finish = Finish;
        limit = Limit;
        available = Available;
        logo_url = Logo_Url;
    }

    public String getId(){return id;}
    public boolean getAssisted(){return assisted;}
    public String getEvent_id(){return event_id;}
    public String getInstructor_id(){return instructor_id;}
    public String getLocation_id(){return location_id;}
    public String getDate(){return date;}
    public String getRoom(){return room;}
    public String getDuration(){return duration;}
    public String getFinish(){return finish;}
    public String getLimit(){return limit;}
    public Boolean getAvailable(){return available;}
    public String getLogo_url(){return logo_url;}
    public String getEvent_name(){return event_name;}
    public String getEvent_description(){return event_description;}
    public String getLocation_name(){return location_name;}
    public String getLocation_adress(){return location_adress;}


}

