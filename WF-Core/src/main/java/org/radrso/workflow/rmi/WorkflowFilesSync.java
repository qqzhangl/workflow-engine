package org.radrso.workflow.rmi;

import org.radrso.workflow.entities.response.WFResponse;

import java.io.Serializable;

/**
 * Created by raomengnan on 17-1-4.
 */
public interface WorkflowFilesSync extends Serializable{
    WFResponse checkAndImportJar(String application, String jarName);
}
