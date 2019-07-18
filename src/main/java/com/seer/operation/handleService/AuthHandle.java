package com.seer.operation.handleService;

import com.seer.operation.entity.AccountAuthPo;
import com.seer.operation.rpcClient.operation.CreateAccountVo;
import com.seer.operation.service.AccountAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthHandle {
    @Autowired
    private AccountAuthService authService;

    @Transactional
    public void initAuth(CreateAccountVo accountVo) {
        if (null != authService.selectById(accountVo.getId())) {
            return;
        }
        AccountAuthPo authPo = new AccountAuthPo();
        authPo.setId(accountVo.getId());
        authPo.setName(accountVo.getName());
        authPo.setOwnerWeightThreshold(accountVo.getOwnerWeightThreshold().intValue());
        authPo.setOwnerAccountAuths(accountVo.getOwnerAccountAuths().toString());
        authPo.setOwnerKeyAuths(accountVo.getOwnerKeyAuths().toString());
        authPo.setOwnerAddressAuths(accountVo.getOwnerAddressAuths().toString());
        authPo.setActiveWeightThreshold(accountVo.getActiveWeightThreshold().intValue());
        authPo.setActiveKeyAuths(accountVo.getActiveKeyAuths().toString());
        authPo.setActiveAddressAuths(accountVo.getActiveAddressAuths().toString());
        authPo.setMemoKey(accountVo.getOptionsMemoKey());
        authPo.setVotingAccount(accountVo.getOptionsVotingAccount());
        authPo.setNumCommittee(accountVo.getOptionsNumCommittee().intValue());
        authPo.setNumAuthenticator(accountVo.getOptionsNumAuthenticator().intValue());
        authPo.setNumSupervisor(accountVo.getOptionsNumSupervisor().intValue());
        authPo.setVotes(accountVo.getOptionsVotes().toString());
        authService.insert(authPo);
    }

    public void updateAuth() {
        //todo update auth by operate=5
    }
}
