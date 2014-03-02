package il.ac.shenkar.remember_to_do;

import android.net.Uri;

/**
 * Created by Owner on 06/01/14.
 */
public class Task {
    private Long id;
    private String title;
    private String date;
    private Uri imageUri;
    private boolean isPriority;
    private String location;

    public Task(){}

    public Task(Long id, String title, String date, String location, Uri imageUri, boolean isPriority){
        setId(id);
        setTitle(title);
        setDate(date);
        setImageUri(imageUri);
        setPriority(isPriority);
        setLocation(location);
    }


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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
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
}
