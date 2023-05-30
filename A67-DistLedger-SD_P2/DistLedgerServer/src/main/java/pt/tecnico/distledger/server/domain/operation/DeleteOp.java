package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;

public class DeleteOp extends Operation {

    public DeleteOp(String account) {
        super(account);
        this.setType(DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT);
    }
}