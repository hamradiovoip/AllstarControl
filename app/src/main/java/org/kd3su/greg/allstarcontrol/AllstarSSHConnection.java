package org.kd3su.greg.allstarcontrol;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;
import com.jcraft.jsch.*;
import java.io.*;
import static java.lang.Thread.sleep;


/**
 * Created by greg on 6/1/2016.
 * For connecting to allstar node via ssh
 */
public class AllstarSSHConnection {

        JSch jsch= null; /* ssh communication*/
        String user= null; /* user name */
        String passwd;   /* pass word for logging in via ssh*/
        String host=null; /* host name */
        Session session; /* ssh session*/
        Channel channel; /* ssh channel */
        int port; /* ssh port */

      /*
        Constructor
       */
        public AllstarSSHConnection()
        {

        }


        /*
           executeRemoteCommand() -  send command to remote Linux machine via ssh
        */
        public static String executeRemoteCommand(String source, String username, String password, String hostname, int port, String cmd)
                throws Exception {
             String results;

           // System.out.println(" Start SSH host= "+hostname+" user= "+username+" port= "+port+ "cmd:"+ cmd);
           try {
               JSch jsch = new JSch();
               Session session = jsch.getSession(username, hostname, port);
               session.setPassword(password);

               // Avoid asking for key confirmation
               Properties prop = new Properties();
               prop.put("StrictHostKeyChecking", "no");
               session.setConfig(prop);

               session.connect(30000);

               // SSH Channel
               ChannelExec myChannel = (ChannelExec) session.openChannel("exec");

               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               myChannel.setOutputStream(baos);

               // Execute command
               myChannel.setCommand(source + cmd);

               myChannel.connect();

               while(!myChannel.isClosed())
               {
                   sleep(100);
               }
               sleep(100);
               myChannel.disconnect();
               session.disconnect();
               results = baos.toString();
           }
           catch(Exception e)
           {
              results = e.getMessage() + ": Error, check your settings or bad connection";
           }
            return results;
        }

        /**
         * Connect to the node
         *
         */
        public boolean connectToMyNode(String user, String passwd, String host, int port)
        {

            this.user = user;
            this.passwd= passwd;
            this.host= host;
            this.port = port;
            String userInput,
                    tempHost="";

            try
            {
                jsch=new JSch();
               // System.out.println("host= "+host+" user= "+user+" port= "+port+" tempHost= "+tempHost);

                session=jsch.getSession(user, host, port);

                session.connect();
            }
            catch(Exception e)
            {
                // TODO need better error handling
                System.out.println(e);
                return false;
            }

            return true;
        }/* connectToMyNode connect via SSH */



    /**
     * another way
     *
     */
        public boolean sendCmd(String command)
        {

            if(command !=null)
            {
                try
                {
                    channel=session.openChannel("exec");
                    ((ChannelExec)channel).setCommand(command);

                    channel.setInputStream(null);

                    ((ChannelExec)channel).setErrStream(System.err);

                    InputStream in= channel.getInputStream();

                    channel.connect();

                    byte[] tmp=new byte[1024];
                    while(true){
                        while(in.available()>0)
                        {
                            int i=in.read(tmp, 0, 1024);
                            if(i<0)break;
                            {
                                String outStr= new String(tmp, 0, i);
                                System.out.print(outStr);
                            }
                        }
                        if(channel.isClosed())
                        {
                            System.out.println("exit-status: "+channel.getExitStatus());
                            break;
                        }
                        try
                        {
                            sleep(1000);
                        }
                        catch(Exception ee){System.out.println("Error with sending command to remote Allstar server\n");}
                    }


                    channel.disconnect();
                    //  session.disconnect();

                }
                catch(Exception e)
                {
                    System.out.println(e);
                    System.out.println("Error ");
                    return false;
                }

            }
            return true;

        }



    }/* file AllstarSSHConnection */




