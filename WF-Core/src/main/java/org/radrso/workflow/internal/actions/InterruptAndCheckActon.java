package org.radrso.workflow.internal.actions;

/**
 * Created by rao-mengnan on 2017/5/18.
 */

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lombok.extern.log4j.Log4j;
import org.radrso.workflow.base.Commander;
import org.radrso.workflow.constant.ExceptionCode;
import org.radrso.workflow.entities.wf.WorkflowErrorLog;
import org.radrso.workflow.entities.wf.WorkflowInstance;
import org.radrso.workflow.resolvers.FlowResolver;

import java.util.Date;

/**
 * 中断和检查中断的动作
 */
@Log4j
public class InterruptAndCheckActon extends AbstractAction implements Consumer<FlowResolver> {

    public InterruptAndCheckActon(Commander workflowSynchronize) {
        super(workflowSynchronize);
    }

    @Override
    public void accept(@NonNull FlowResolver resolver) throws Exception {
        String instanceId = resolver.getWorkflowInstance().getInstanceId();
        operations.interruptInstanceProcess(instanceId);

        boolean interrupted = false;
        WorkflowInstance originInstanceInfo = commander.getInstance(instanceId);
        int branches = originInstanceInfo.getBranches();
        long brockTime = 1000 * 60 * 5; // 超时时间为五分钟
        long beginTime = System.currentTimeMillis();

            /*当轮询检查所有的分支，直到没有分支处于RUNNING状态才退出检查*/
        while (!interrupted) {
            if (brockTime < System.currentTimeMillis() - beginTime) {
                break;
            }

            if (originInstanceInfo.getStatus().equals(WorkflowInstance.RUNNING)){
                originInstanceInfo =  commander.getInstance(instanceId);
                continue;
            }

            interrupted = true;
            for (int i = 1; i <= branches; i++){
                String id = instanceId + "-" + i;
                WorkflowInstance instanceBranch = commander.getInstance(id);
                if (instanceBranch.getStatus().equals(WorkflowInstance.RUNNING)) {
                    interrupted = false;
                    break;
                }
            }
        }
        if (!interrupted){
            WorkflowErrorLog errorLog = new WorkflowErrorLog(
                    null,
                    ExceptionCode.INTERRUPT_EXCEPTION.code(),
                    resolver.getWorkflowInstance().getWorkflowId(),
                    instanceId,
                    null,
                    "Interrupt timeout error",
                    new Date(),
                    null
            );
            commander.saveErrorLog(errorLog);
            log.error(errorLog);
        }
        else {
            log.info("Interrupted: " + instanceId);
        }
    }



    @Override
    public Action setResolver(FlowResolver resolver) {
        return this;
    }
}
