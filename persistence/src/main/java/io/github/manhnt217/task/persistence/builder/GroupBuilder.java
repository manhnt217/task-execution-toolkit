package io.github.manhnt217.task.persistence.builder;

import io.github.manhnt217.task.core.activity.Activity;
import io.github.manhnt217.task.core.activity.group.Group;
import io.github.manhnt217.task.core.activity.simple.EndActivity;
import io.github.manhnt217.task.core.activity.simple.StartActivity;
import io.github.manhnt217.task.core.exception.inner.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author manhnguyen
 */
class GroupBuilder {
    protected String startName;
    protected String endName;
    protected List<ImmutablePair<Activity, String>> startLinks;
    protected List<ImmutablePair<Activity, String>> endLinks;
    protected List<String> startToEndLinks;
    protected List<ImmutablePair<Activity, ImmutablePair<Activity, String>>> links;
    protected String outputMapping;

    protected StartActivity startActivity;
    protected EndActivity endActivity;

    GroupBuilder() {

        this.startLinks = new ArrayList<>();
        this.endLinks = new ArrayList<>();
        this.startToEndLinks = new ArrayList<>();
        this.links = new ArrayList<>();
    }


    void start(String name) {
        this.startName = name;
    }

    void end(String name) {
        this.endName = name;
    }

    void linkFromStart(Activity a, String guard) {
        startLinks.add(ImmutablePair.of(a, guard));
    }

    void linkFromStart(Activity a) {
        this.linkFromStart(a, null);
    }

    void linkToEnd(Activity a, String guard) {
        endLinks.add(ImmutablePair.of(a, guard));
    }

    void linkToEnd(Activity a) {
        this.linkToEnd(a, null);
    }

    void linkStartToEnd(String guard) {
        this.startToEndLinks.add(guard);
    }

    void link(Activity a, Activity b, String guard) {
        links.add(ImmutablePair.of(a, ImmutablePair.of(b, guard)));
    }

    void link(Activity a, Activity b) {
        this.link(a, b, null);
    }

    void outputMapping(String outputMapping) {
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

        for (String guard : startToEndLinks) {
            group.linkStartToEnd(guard);
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
