package il.ac.shenkar.remember_to_do;

import android.net.Uri;

/**
 * the task DM
 */
public class Task {
    private Long id;
    private String title;
    private String date;
    private Uri imageUri;
    private boolean isPriority;
    private String location;
    private String imagePath;
    private boolean isPicFromCam;

    public Task(){}

    /**
     * Constructor
     * @param id - task id
     * @param title - task title
     * @param date - task date
     * @param location - task location
     * @param imageUri - task attach Image Uri
     * @param isPriority - boolean if its high priority
     * @param imgPath - the img path
     * @param isPicFromCam - if the pic that taken is from the camera or gallery
     */
    public Task(Long id, String title, String date, String location, Uri imageUri, boolean isPriority, String imgPath, boolean isPicFromCam){
        setId(id);
        setTitle(title);
        setDate(date);
        setImageUri(imageUri);
        setPriority(isPriority);
        setLocation(location);
        setImagePath(imgPath);
        setPicFromCam(isPicFromCam);
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isPicFromCam() {
        return isPicFromCam;
    }

    public void setPicFromCam(boolean isCamera) {
        this.isPicFromCam = isCamera;
    }
}
