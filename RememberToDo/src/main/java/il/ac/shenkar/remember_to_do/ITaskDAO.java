package il.ac.shenkar.remember_to_do;

import java.text.ParseException;
import java.util.List;

/**
 * This interface holds methods
 * which are implemented in ProjectDAO class
 * @author Dror Afargan & Ran Nahmijas
 */
public interface ITaskDAO {
    /**
     * Adds new ic_task_board to the DB
     * @param taskObj (ic_task_board)
     */
    public abstract void addTask(Task taskObj);

    /**
     * Delete ic_task_board from DB
     * @param taskObj (ic_task_board)
     */
    public abstract void deleteTask(Task taskObj);

    /**
     *
     * @return List of all tasks
     * @throws ParseException
     */
    public abstract List<Task> getAllTasks( boolean isImportant)throws ParseException;

    /**
     *delete all the tasks from DB
     */
    public void deleteAllTasks();
}
