package il.ac.shenkar.remember_to_do;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("taskItem")
public class TaskForCloud extends ParseObject {

    // a public default constructor
    public TaskForCloud() {
        super();
    }

    // constructor that contains core properties
    public TaskForCloud( String title, String date, String location, String isPriority, String notes){
        setTitle(title);
        setDate(date);
        setIsPriority(isPriority);
        setLocation(location);
        setNotes(notes);
    }

    // constructor that contains core properties
    public TaskForCloud(Task task, String userName){
        if (task!=null) {
            setTitle(task.getTitle());
            setDate(task.getDate());
            String priority = "false";
            if (task.isPriority()) {
                priority = "true";
            }
            setIsPriority(priority);
            setLocation(task.getLocation());
            setNotes(task.getNotes());

            setUserName(userName);
        }
    }

    // getters and setters
    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String value) {
        put("title", value);
    }

    public String getLocation() {
        return getString("location");
    }

    public void setLocation(String value) {
        put("location", value);
    }

    public String getDate() {
        return getString("date");
    }

    public void setDate(String value) {
        put("date", value);
    }

    public String getNotes() {
        return getString("notes");
    }

    public void setNotes(String value) {
        put("notes", value);
    }

    public String getIsPriority() {
        return getString("isPriority");
    }

    public void setIsPriority(String value) {
        put("isPriority", value);
    }

    public String getUserName() {
        return getString("user_name");
    }

    public void setUserName(String value) {
        put("user_name", value);
    }

    // Get the user for this item
    public ParseUser getUser()  {
        return getParseUser("owner");
    }

    // Associate each item with a user
    public void setOwner(ParseUser user) {
        put("owner", user);
    }
}