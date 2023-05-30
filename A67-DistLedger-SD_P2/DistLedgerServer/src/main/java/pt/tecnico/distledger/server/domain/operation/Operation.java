package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;

public class Operation {

    private DistLedgerCommonDefinitions.OperationType type;
    private String account;

    public Operation(String account) {
        this.account = account;
    }

    public DistLedgerCommonDefinitions.OperationType getType() { return type; }

    public void setType(DistLedgerCommonDefinitions.OperationType type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}