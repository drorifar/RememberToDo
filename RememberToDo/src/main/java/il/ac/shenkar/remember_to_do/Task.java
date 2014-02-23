package il.ac.shenkar.remember_to_do;

/**
 * Created by Owner on 06/01/14.
 */
public class Task {
    private Long id;
    private String title;
    private String description;
    private String date;

    public Task(){}

    public Task(Long id, String title, String description, String date){
        setId(id);
        setTitle(title);
        setDescription(description);
        setDate(date);
    }


    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}
