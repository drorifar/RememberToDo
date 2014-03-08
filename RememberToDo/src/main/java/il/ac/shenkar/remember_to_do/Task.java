package il.ac.shenkar.remember_to_do;

import android.net.Uri;

/**
 * the task DM
 */
public class Task {
    private Long id;
    private String title;
    private String date;
    private boolean isPriority;
    private String location;
    private String notes;

    public Task(){}

    /**
     * Constructor
     * @param id - task id
     * @param title - task title
     * @param date - task date
     * @param location - task location
     * @param isPriority - boolean if its high priority
     */
    public Task(Long id, String title, String date, String location, boolean isPriority, String notes){
        setId(id);
        setTitle(title);
        setDate(date);
        setPriority(isPriority);
        setLocation(location);
        setNotes(notes);
    }

    //setters and getters

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean isPriority) {
        this.isPriority = isPriority;
    }

    public void setPriorityFromString(String stringPriority) {
        if (stringPriority.equals("true"))
            this.isPriority = true;
        else this.isPriority = false;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
