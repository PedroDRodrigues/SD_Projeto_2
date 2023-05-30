package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ServerState {

    private List<Operation> ledger;
    private Map<String, Integer> accounts;
    private int active;

    public ServerState() {
        this.ledger = new ArrayList<>();
        this.accounts = new HashMap<>();
        this.accounts.put("broker",1000);
        this.active = 1;
    }

    public List<Operation> getLedger() {
        return ledger;
    }

    public void setLedger(List<Operation> ledger) {
        this.ledger = ledger;
    }

    public Map<String, Integer> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<String, Integer> accounts) {
        this.accounts = accounts;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public void addOperation(Operation operation) {
        this.ledger.add(operation);
    }

    public void removeLastOperation() {
        if (this.ledger.size() > 0)
            this.ledger.remove(this.ledger.size()-1);
    }

    //Check if this userId already has an account and if not creates it
    public synchronized int createAccount(String userId, int balance) {
        if (this.accounts.containsKey(userId))
            return -1;
        addOperation(new CreateOp(userId));
        this.accounts.put(userId,balance);
        return 1;
    }

    // Check if this userId exists and has balance >= 0 and after it has been verified, delete it from the map
    public synchronized int deleteAccount(String userId) {                    
        if (userId.equals("broker"))
            return -3;
        int balance = this.balance(userId);
        if (balance > 0)
            return -2;
        else if (balance < 0)
            return -1;
        else{
            this.accounts.remove(userId);
            addOperation(new DeleteOp(userId));
            return 1;
        }
    }

    //Returns the balance of account if account exists or -1 if the account doesnt exists
    public synchronized int balance(String userId) {                             
        if (!this.accounts.containsKey(userId))
            return -1;
        return this.accounts.get(userId);
    }

    //Verify the existence of both accounts (from and destination) and if fromAccount's balance is enough to realize the operation 
    public synchronized int transferTo(String userFrom, String userTo, int amount) {          
        int balance1 = balance(userFrom);
        int balance2 = balance(userTo);
        if (balance1 < 0)
            return -1;
        if (balance2 < 0)
            return -2;
        if (balance1 < amount)
            return -3;
        addOperation(new TransferOp(userFrom, userTo, amount));
        this.accounts.put(userFrom,balance1-amount);
        this.accounts.put(userTo,balance2 + amount);
        return 1;
    }

    public synchronized void activate() {
        this.active = 1;
    }

    public synchronized void deactivate() {
        this.active = 0;
    }

    @Override
    public String toString() {
        return "ServerState{" +
                "ledger=" + ledger +
                "accounts=" + accounts +
                '}';
    }
}