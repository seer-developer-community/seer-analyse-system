package com.seer.operation.rpcClient.response;

import com.seer.operation.rpcClient.operation.GlobalParametersVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetGlobal implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private GlobalParametersVo parameters;
    private Integer nextAvailableVoteId;
    private List activeCommitteeMembers;
    private List activeWitnesses;
    private List activeCollateralWitnesses;
    private List activeSupervisors;
    private List activeAuthenticators;
    private Boolean seerExploded;
}
