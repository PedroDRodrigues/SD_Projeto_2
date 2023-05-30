package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*; 
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.domain.operation.*;

import io.grpc.stub.StreamObserver;
import java.util.*;

public class SecondaryServerService extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private ServerState serverState;

    public SecondaryServerService(ServerState serverState) {
        this.serverState = serverState;
    }

    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {

        PropagateStateResponse response;

        if (this.serverState.getActive() != 1) {
            response = PropagateStateResponse.newBuilder().setError(ErrorPropagate.UNAVAILABLE).build();
        }
        else {
            DistLedgerCommonDefinitions.Operation operation_proto = request.getLedgerState().getLedgerList().get(0);

            if (operation_proto.getType() == OperationType.OP_CREATE_ACCOUNT)
                this.serverState.createAccount(operation_proto.getAccount(), 0);
            else if (operation_proto.getType() == OperationType.OP_DELETE_ACCOUNT)
                this.serverState.deleteAccount(operation_proto.getAccount());
            else if(operation_proto.getType() == OperationType.OP_TRANSFER_TO)
                this.serverState.transferTo(operation_proto.getAccount(), operation_proto.getDestAccount(), operation_proto.getAmount());
            
            response = PropagateStateResponse.newBuilder().setError(ErrorPropagate.OK).build();
        } 
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}