package agent;

import java.io.IOException;

public class AgentStart {

    public static void main(String[] args) {
        try {
            System.out.println("Agent handler process is starting, collecting external data.");
            String[] inputFromUser = args[0].split(":");
            String ipAddress = inputFromUser[0];
            int port = Integer.valueOf(inputFromUser[1]);

            System.out.println("Agent handler collected external data successfully.");
            AgentHandler agentHandler = new AgentHandler(ipAddress, port);
            System.out.println("Agent handler initialize agent socket successfully.");
            agentHandler.run();
        }
        catch (IOException e) {
            System.out.println("There was a problem with connecting to sockets, process can't continue aborting.");
        }
    }
}
