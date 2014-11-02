package hu.teteny.labor.rfidauthenticatorserver;


public class App 
{
    public static void main( String[] args )
    {
        DatabaseHandler.getInstance("test.db");
        //no arguments just config file
        TCPServer server=new TCPServer(4000);
        server.start();
    }
}
