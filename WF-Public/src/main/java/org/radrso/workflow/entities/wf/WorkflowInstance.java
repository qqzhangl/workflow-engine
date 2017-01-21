package org.radrso.workflow.entities.wf;

import lombok.Data;
import lombok.ToString;
import org.radrso.workflow.entities.response.WFResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by raomengnan on 17-1-13.
 */
@Data
@ToString
@Document(collection = "workflowInstance")
public class WorkflowInstance implements Serializable{
    public static final String EXPIRED = "expired";
    public static final String RUNNING = "running";
    public static final String COMPLETED = "completed";
    public static final String EXCEPTION = "exception";

    private String workflowId;
    @Id
    private String instanceId;
    private Date createTime = new Date();
    private Date submitTime;
    private String status = RUNNING;

    private int branchs = 0;
    private Map<Integer, String> branchStepMap;

    private ConcurrentHashMap<String, String> stepProcess;
    private ConcurrentHashMap<String, Object[]> stepParams;
    private ConcurrentHashMap<String, String[]> stepParamNames;
    private ConcurrentHashMap<String, WFResponse> stepResponses;

    private List<String> finishedSequence;

    public WorkflowInstance(String workflowId, String instanceId){
        this();
        this.workflowId = workflowId;
        this.instanceId = instanceId;
    }
    private WorkflowInstance(){
        this.stepProcess = new ConcurrentHashMap<>();
        this.stepParams = new ConcurrentHashMap<>();
        this.stepResponses = new ConcurrentHashMap<>();
        this.stepParamNames = new ConcurrentHashMap<>();

        this.branchStepMap = new HashMap<>();
        this.finishedSequence = new ArrayList<>();
    }
}
