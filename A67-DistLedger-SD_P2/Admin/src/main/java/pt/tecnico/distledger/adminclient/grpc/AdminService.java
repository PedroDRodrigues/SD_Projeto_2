package pt.tecnico.distledger.adminclient.grpc;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.*;

public class AdminService {

    private ManagedChannel channel_naming, channel;

    private AdminServiceGrpc.AdminServiceBlockingStub stub;
    private NamingServerServiceGrpc.NamingServerServiceBlockingStub stub_lookup;

    public AdminService(String host, int port) {
        this.channel_naming = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.stub_lookup = NamingServerServiceGrpc.newBlockingStub(this.channel_naming);
    }

    public void activate(String qualificator) {
        ActivateRequest request = ActivateRequest.newBuilder().build();

        find_connection(qualificator);

        ActivateResponse response = this.stub.activate(request);
        channel.shutdown();
        System.out.println("OK");
    }

    public void deactivate(String qualificator) {
        DeactivateRequest request = DeactivateRequest.newBuilder().build();

        find_connection(qualificator);

        DeactivateResponse response = this.stub.deactivate(request);
        channel.shutdown();
        System.out.println("OK");
    }

    public void getLedgerState(String qualificator) {
        getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();

        find_connection(qualificator);

        getLedgerStateResponse response = this.stub.getLedgerState(request);
        channel.shutdown();
        System.out.println("OK");
        for(DistLedgerCommonDefinitions.LedgerState ledgerState : response.getLedgerStateList()) {
            System.out.println("ledgerState {");
            for(DistLedgerCommonDefinitions.Operation ledger : ledgerState.getLedgerList()) {
                System.out.println("  ledger {");
                DistLedgerCommonDefinitions.OperationType type = ledger.getType();
                System.out.println("    type: " + type);
                System.out.println("    userId: \"" + ledger.getAccount() + "\"");
                if(type == DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO) {
                    System.out.println("    destUserId: \"" + ledger.getDestAccount() + "\"");
                    System.out.println("    amount: \"" + ledger.getAmount() + "\"");
                }
                System.out.println("  }");
            }
            System.out.println("}");
        }
    }

    public void find_connection(String qualificator) {
        List<String> servers = new ArrayList<String>();
        servers = lookup("DistLedger",qualificator);
        if (servers.size() == 0)
            return;
        
        String[] target = servers.get(0).split(":");
        String host = target[0];
        int port = Integer.parseInt(target[1]);

        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.stub = AdminServiceGrpc.newBlockingStub(this.channel);
    }

    /* TODO Phase-3 */
    public void gossip() {}

    public List<String> lookup(String serviceName, String qualificator) {
        LookupRequest request = LookupRequest.newBuilder().setServiceName(serviceName).setQualificator(qualificator).build();

        LookupResponse response = this.stub_lookup.lookup(request);

        return response.getTargetList();
    }

    public void shutdown() {
        this.channel_naming.shutdown();
    }
}