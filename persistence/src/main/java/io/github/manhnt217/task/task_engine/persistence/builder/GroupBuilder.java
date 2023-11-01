package io.github.manhnt217.task.task_engine.persistence.builder;

import io.github.manhnt217.task.task_engine.activity.Activity;
import io.github.manhnt217.task.task_engine.activity.group.Group;
import io.github.manhnt217.task.task_engine.activity.simple.EndActivity;
import io.github.manhnt217.task.task_engine.activity.simple.StartActivity;
import io.github.manhnt217.task.task_engine.exception.inner.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author manhnguyen
 */
public class GroupBuilder {
    protected String startName;
    protected String endName;
    protected List<ImmutablePair<Activity, String>> startLinks;
    protected List<ImmutablePair<Activity, String>> endLinks;
    protected List<ImmutablePair<Activity, ImmutablePair<Activity, String>>> links;
    protected String outputMapping;

    protected StartActivity startActivity;
    protected EndActivity endActivity;

    public GroupBuilder() {

        this.startLinks = new ArrayList<>();
        this.endLinks = new ArrayList<>();
        this.links = new ArrayList<>();
    }


    public void start(String name) {
        this.startName = name;
    }

    public void end(String name) {
        this.endName = name;
    }

    public void linkFromStart(Activity a, String guard) {
        startLinks.add(ImmutablePair.of(a, guard));
    }

    public void linkFromStart(Activity a) {
        this.linkFromStart(a, null);
    }

    public void linkToEnd(Activity a, String guard) {
        endLinks.add(ImmutablePair.of(a, guard));
    }

    public void linkToEnd(Activity a) {
        this.linkToEnd(a, null);
    }

    public void link(Activity a, Activity b, String guard) {
        links.add(ImmutablePair.of(a, ImmutablePair.of(b, guard)));
    }

    public void link(Activity a, Activity b) {
        this.link(a, b, null);
    }

    public void outputMapping(String outputMapping) {
        this.outputMapping = outputMapping;
    }

    Group buildGroup() throws ConfigurationException {
        this.startActivity = new StartActivity(startName);
        this.endActivity = new EndActivity(endName);
        endActivity.setInputMapping(outputMapping);

        Group group = new Group();
        group.addActivity(startActivity);
        group.addActivity(endActivity);

        for (ImmutablePair<Activity, String> startLink : startLinks) {
            group.linkFromStart(startLink.getLeft(), startLink.getRight());
        }

        for (ImmutablePair<Activity, String> endLink : endLinks) {
            group.linkToEnd(endLink.getLeft(), endLink.getRight());
        }

        for (ImmutablePair<Activity, ImmutablePair<Activity, String>> link : links) {
            group.linkActivities(link.getLeft(), link.getRight().getLeft(), link.getRight().getRight());
        }

        return group;
    }

    void validate() {
        if (StringUtils.isBlank(startName)) {
            throw new IllegalArgumentException("Activity's start name should not be empty");
        }
        // TODO: implement other validations
    }
}
