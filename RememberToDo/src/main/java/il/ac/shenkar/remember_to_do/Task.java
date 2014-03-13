package il.ac.shenkar.remember_to_do;


/**
* This class represent Task object
* @author Dror Afargan & Ran Nahmijas
*/
public class Task
{
    //ic_task_board id
    private Long id;
    //ic_task_board title
    private String title;
    //ic_task_board date
    private String date;
    //if the ic_task_board is in priority
    private boolean isPriority;
    //ic_task_board location
    private String location;
    //extra notes
    private String notes;

    public Task(){}

    /**
     * Constructor
     * @param id - ic_task_board id
     * @param title - ic_task_board title
     * @param date - ic_task_board date
     * @param location - ic_task_board location
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

    public Task(TaskForCloud taskFromCloud){
        setId(System.currentTimeMillis());
        setTitle(taskFromCloud.getTitle());
        setDate(taskFromCloud.getDate());
        setPriorityFromString(taskFromCloud.getIsPriority());
        setLocation(taskFromCloud.getLocation());
        setNotes(taskFromCloud.getNotes());
    }

    /**
     * setters and getters
     */

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
