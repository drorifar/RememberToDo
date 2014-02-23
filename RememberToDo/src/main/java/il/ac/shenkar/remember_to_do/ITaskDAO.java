package il.ac.shenkar.remember_to_do;

import java.text.ParseException;
import java.util.List;

/**
 * This interface holds methods
 * which are implemented in ProjectDAO class
 */
public interface ITaskDAO {
    /**
     * Adds new task to the DB
     * @param taskObj (task)
     */
    public abstract void addTask(Task taskObj);

    /**
     * Delete task from DB
     * @param taskObj (task)
     */
    public abstract void deleteTask(Task taskObj);

    /**
     *
     * @return List of all tasks
     * @throws ParseException
     */
    public abstract List<Task> getAllTasks()throws ParseException;
}
