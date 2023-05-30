# DistLedger

Distributed Systems Project 2022/2023

## Authors

**Group A67**

### Code Identification

In all source files (namely in the *groupId*s of the POMs), replace __GXX__ with your group identifier. The group
identifier consists of either A or T followed by the group number - always two digits. This change is important for 
code dependency management, to ensure your code runs using the correct components and not someone else's.

### Team Members

| Number | Name              |           User                       |              Email                               |
|--------|-------------------|--------------------------------------|--------------------------------------------------|
| 99340  | Tomás Marques     | <https://github.com/tomas1610>       | <mailto:tomasduarte1610@tecnico.ulisboa.pt>      |
| 99300  | Pedro Rodrigues   | <https://github.com/PedroDRodrigues> | <mailto:pedro.dias.rodrigues@tecnico.ulisboa.pt> |
| 80949  | Ramiro Gomes      | <https://github.com/ramiromgomes>    | <mailto:ramiro.m.gomes@tecnico.ulisboa.pt>       |

## Getting Started

The overall system is made up of several modules. The main server is the _DistLedgerServer_. The clients are the _User_ 
and the _Admin_. The definition of messages and services is in the _Contract_. The future naming server
is the _NamingServer_.

See the [Project Statement](https://github.com/tecnico-distsys/DistLedger) for a complete domain and system description.

### Prerequisites

The Project is configured with Java 17 (which is only compatible with Maven >= 3.8), but if you want to use Java 11 you
can too -- just downgrade the version in the POMs.

To confirm that you have them installed and which versions they are, run in the terminal:

```s
javac -version
mvn -version
```

### Installation

To compile and install all modules:

```s
mvn clean install
```

## Built With

* [Maven](https://maven.apache.org/) - Build and dependency management tool;
* [gRPC](https://grpc.io/) - RPC framework.


## Testing

Enter the DistLedgerServer directory and run:

```s
mvn exec:java -Dexec.args="2001 A"
```

Open a new terminal and enter the Admin directory and run:

```s
mvn exec:java -Dexec.args="localhost 2001"
```

Open a new terminal and enter the User directory and run:

```s
mvn exec:java -Dexec.args="localhost 2001"
```

Now, in the Admin terminal, you can run the following commands:
To activate a server run:

```s
activate A
```

To deactivate a server run:

```s
deactivate A
```

To getLedgerState run:

```s
getLedgerState A
```

Now, in the User terminal, you can run the following commands:
To create a new account run:

```s
createAccount A user_name
```

To get the balance of an account run:

```s
balance A user_name
```

To transfer 100 from one account (user_name) to another (user_name_1) run:

```s
createAccount A user_name_1
transferTo A user_name_1 user_name_2 100
```

To delete an account run:

```s
deleteAccount A user_name
```