package org.dataconservancy.cos.osf.client.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jasminb.jsonapi.RelType;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;
import org.dataconservancy.cos.osf.client.support.DateTimeTransform;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.joda.time.DateTime;

import java.util.Map;

import static org.dataconservancy.cos.osf.client.support.JodaSupport.DATE_TIME_FORMATTER;

/**
 * Encapsulates an event in the OSF.  Events in the OSF are expressed as the JSON-API type "logs".
 */
@Type("logs")
@OwlIndividual(OwlClasses.OSF_EVENT)
public class Event {

    @Id
    @IndividualUri
    private String id;

    @OwlProperty(value = OwlProperties.OSF_HAS_EVENT_DATE, transform = DateTimeTransform.class)
    private DateTime date;

    // TODO: verify that osf:eventTarget is the correct semantics for 'node'
    @Relationship(value = "node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(OwlProperties.OSF_HAS_EVENT_TARGET)
    private String node;

    // TODO: verify that osf:eventSource is the correct semantics for 'original_node'
    @Relationship(value = "original_node", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    @OwlProperty(OwlProperties.OSF_HAS_EVENT_SOURCE)
    private String original_node;

    @Relationship(value = "contributors", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String contributors;

    @Relationship(value = "user", resolve = true, relType = RelType.RELATED, strategy = ResolutionStrategy.REF)
    private String user;

    @OwlProperty(OwlProperties.OSF_HAS_EVENT_ACTION)
    private String action;

    private Map<String, ?> params;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        if (this.date != null) {
            return date.toString(DATE_TIME_FORMATTER);
        }
        return null;
    }

    public void setDate(String date) {
        this.date = DATE_TIME_FORMATTER.parseDateTime(date);
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getOriginal_node() {
        return original_node;
    }

    public void setOriginal_node(String original_node) {
        this.original_node = original_node;
    }

    public String getContributors() {
        return contributors;
    }

    public void setContributors(String contributors) {
        this.contributors = contributors;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, ?> getParams() {
        return params;
    }

    public void setParams(Map<String, ?> params) {
        this.params = params;
    }
}
