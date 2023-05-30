package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ServerMain {
    
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(ServerMain.class.getSimpleName());
        
        final int port = Integer.parseInt(args[0]);
        final String qualificator = args[1];

        ServerState serverState = new ServerState();
        DistLedgerCrossServerService serverService = new DistLedgerCrossServerService(5001);

        // Create a new server to listen on port
        Server server;
        if (qualificator.equals("A"))
            server = ServerBuilder.forPort(port).addService(new UserService(serverState,serverService,qualificator)).addService(new AdminService(serverState,serverService,qualificator)).build();
        else if (qualificator.equals("B"))
            server = ServerBuilder.forPort(port).addService(new SecondaryServerService(serverState)).addService(new UserService(serverState,serverService,qualificator)).addService(new AdminService(serverState,serverService,qualificator)).build();
        else {
            System.out.println("Qualificator not valid");
            return;
        }
    
        // Start the server
        server.start();
        serverService.register("DistLedger",qualificator,"localhost:"+port);
        
        // Server threads are running in the background.
        System.out.println("Server started");

        System.out.println("Press enter to shutdown");
        System.in.read();
        server.shutdown();
        
        serverService.delete("DistLedger","localhost:" + port);
    }
}