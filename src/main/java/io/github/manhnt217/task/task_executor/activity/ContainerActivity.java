package io.github.manhnt217.task.task_executor.activity;

public interface ContainerActivity extends Activity {

    String BLANK_GUARD_EXP = "<blank>";

    /**
     * Add activity to the container.
     * If the activity has already been added, nothing happens.
     * @param activity
     */
    void addActivity(Activity activity);

    /**
     * Link 2 activities. Both activities must be added first using {@link #addActivity(Activity)}
     * @param from
     * @param to
     * @param guardExp <br> - jslt expression which at the runtime will be evaluated to true/false.If true, the link will be activated and the next activity (of the link) will be executed.
     *                 When an activity has multiple links to other activities, the first link, which its guard is evaludated to true, will be chosen.
     *                 <br> - <code>null</code> means no guard.
     */
    void linkActivities(Activity from, Activity to, String guardExp);
}
