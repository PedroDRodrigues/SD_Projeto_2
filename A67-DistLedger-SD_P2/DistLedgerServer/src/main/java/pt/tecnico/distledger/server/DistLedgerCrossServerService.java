package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

import java.util.*;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DistLedgerCrossServerService {

    private ManagedChannel channel;
    private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub stub_server_secondary;

    public DistLedgerCrossServerService(int port) {
        this.channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        this.stub = NamingServerServiceGrpc.newBlockingStub(this.channel);
    }

    public void register(String serviceName, String qualificator, String target) {
        RegisterRequest request = RegisterRequest.newBuilder().setServiceName(serviceName).setQualificator(qualificator).setTarget(target).build();

        RegisterResponse response = this.stub.register(request);
    }

    public List<String> lookup(String serviceName, String qualificator) {
        LookupRequest request = LookupRequest.newBuilder().setServiceName(serviceName).setQualificator(qualificator).build();

        LookupResponse response = this.stub.lookup(request);
       
        return response.getTargetList();
    }

    public void delete(String serviceName, String target) {
        DeleteRequest request = DeleteRequest.newBuilder().setServiceName(serviceName).setTarget(target).build();

        DeleteResponse response = this.stub.delete(request);
    }

    public PropagateStateResponse propagateState(List<Operation> ledger) {
        PropagateStateRequest.Builder requestBuilder = PropagateStateRequest.newBuilder();

        DistLedgerCommonDefinitions.LedgerState.Builder ledgerStateBuilder = DistLedgerCommonDefinitions
                .LedgerState.newBuilder();
                
        Operation last_operation = ledger.get(ledger.size() - 1);

        DistLedgerCommonDefinitions.Operation.Builder operationBuilder = DistLedgerCommonDefinitions
        .Operation.newBuilder();

        operationBuilder.setType(last_operation.getType());
        operationBuilder.setAccount(last_operation.getAccount());
        if(last_operation instanceof TransferOp) {
            operationBuilder.setDestAccount(((TransferOp) last_operation).getDestAccount());
            operationBuilder.setAmount(((TransferOp) last_operation).getAmount());
        }        

        ledgerStateBuilder.addLedger(operationBuilder.build());

        PropagateStateRequest request = requestBuilder.setLedgerState(ledgerStateBuilder.build()).build();
        find_connection("B");
        PropagateStateResponse response = this.stub_server_secondary.propagateState(request);
        return response;
    }

    public void find_connection(String qualificator) {
        List<String> servers = new ArrayList<String>();
        servers = lookup("DistLedger",qualificator);
        if (servers.size() == 0)
            return;
        
        String[] target = servers.get(0).split(":");
        String host = target[0];
        int port = Integer.parseInt(target[1]);

        ManagedChannel channel_secondary = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.stub_server_secondary = DistLedgerCrossServerServiceGrpc.newBlockingStub(channel_secondary);
    }
}