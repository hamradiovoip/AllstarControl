package org.kd3su.greg.allstarcontrol;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.os.Message;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;




/*                    <Button
                        style="?android:attr/buttonStyleSmall"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Disconnect All"
                        android:id="@+id/button3" />
                    <Button
                        style="?android:attr/buttonStyleSmall"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Node Status"
                        android:id="@+id/button4" />*/


public class MainActivity extends AppCompatActivity
{
    private static final int RESULT_SETTINGS = 1;
    public static final String PREFS_NAME = "MyPrefsFile";
    public String results = "";

    public AllstarSSHConnection node = new AllstarSSHConnection(); /* ssh connection */

    public String passwd = "";
    public String user = "";
    public String host = "";
    public int port = 222;

    public String insideHost = "";
    public int insidePort = 22;
    String source= " ";
    public String remoteNode = "";
    public String myNode = "";
    public String cmd = "";

    public String allstardb = "/var/log/asterisk/astdb.txt";

    public String connectedNodesCmd = "sudo /usr/sbin/asterisk -rx \"rpt nodes $node\"";
    public String nodeStatusCmd = "sudo /usr/sbin/asterisk -rx \"rpt stats $node\"";
    public String my_ipCmd = "/usr/bin/curl -s icanhazip.com";
    public String nodeLstatusCmd = "sudo /usr/sbin/asterisk -rx \"rpt lstats $node\"";
    public String iaxRegistryCmd = "sudo /usr/sbin/asterisk -rx \"iax2 show registry\"";
    public String systemStatusCmd = "";
    public  TextView textview; /* for displaying results like nodes connected  */
    public boolean connectedNodesFlag = false; /* use for formating list of  list of nodes */
    public boolean nodeStatusFlag = false;
    public boolean connectFlag = false;
    public boolean disConnectFlag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        /*
        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));

*/

        //addPreferencesFromResource(R.xml.settings);

        // buttons
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(myhandler1);

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(myhandler1);
/*
        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(myhandler1);
*/
        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(myhandler1);

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(myhandler1);

        // display text at bottom
        textview = (TextView) findViewById(R.id.textview1);
        textview.setMovementMethod(new ScrollingMovementMethod());
        textview.setText(".");
    }// oncreate



    /*
     * handle events
     */
    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
           // SharedPreferences prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);

            if(prefs == null)
            {
                // System.out.println("Error with settings, check your settings");
                Context context = getApplicationContext();
                CharSequence text = "Error with settings, check your settings";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }

            String usernameStr = prefs.getString("prefUsername", "");
            String domainStr = prefs.getString("prefIp", "");
            String nodeStr = prefs.getString("prefNode", ""); /* your Allstar node number */
            String passwordStr = prefs.getString("prefNodePassword", "");
            String portStr = prefs.getString("prefPort", "");

            boolean errorflag = false;
            if (nodeStr != null || !nodeStr.isEmpty())
                myNode = nodeStr;
            else
            {
                errorflag = true;
                textview.setText("Error: check your node number in settings.");
            }
            if(passwordStr != null || !passwordStr.isEmpty())
                passwd = passwordStr;
            else
            {
                errorflag = true;
                textview.setText("Error: check password in settings.");
            }
            if(user != null || !user.isEmpty())
                user = usernameStr;
            else
            {
                errorflag = true;
                textview.setText("Error: check username in settings.");
            }

            if(domainStr != null || !domainStr.isEmpty())
                host = domainStr;
            else
            {
                errorflag = true;
                textview.setText("Error: check host name in settings.");
            }
            if(portStr != null || !portStr.isEmpty())
                port = Integer.valueOf(portStr);
            else
            {
                errorflag = true;
                textview.setText("Error: check port number in settings. ");
            }

            if(errorflag == false) {
                // get text of node to connect to from user input via text
                EditText remoteNodeED = (EditText) findViewById(R.id.editText);

                if (remoteNodeED != null) {
                    remoteNode = remoteNodeED.getText().toString();
                    // System.out.println("node #"+ remoteNode);
                } else System.out.println("node null");


                connectedNodesCmd = "sudo /usr/sbin/asterisk -rx \"rpt nodes " + myNode + "\"";
                nodeStatusCmd = "sudo /usr/sbin/asterisk -rx \"rpt stats " + myNode + "\"";
                // my_ip = "/usr/bin/curl -s icanhazip.com";
                nodeLstatusCmd = "sudo /usr/sbin/asterisk -rx \"rpt lstats " + myNode + "\"";
                iaxRegistryCmd = "sudo /usr/sbin/asterisk -rx \"iax2 show registry\"";
                systemStatusCmd = "sudo /usr/sbin/asterisk -rx \"rpt cmd " + myNode + " ilink 11 " + remoteNode + " \" \n";

                if (remoteNode != null) {
                    String feedback = "none";
                    switch (v.getId()) {
                        case R.id.button: // connect to node #
                            connectFlag = true;
                            cmd = "sudo /usr/sbin/asterisk -rx \"rpt cmd " + myNode + " ilink 13 " + remoteNode + " \" \n";
                            source = "";
                            feedback = "Connecting...to " + remoteNode;
                            break;

                        case R.id.button2: // disconnect from node #
                            disConnectFlag = true;
                            cmd = "sudo /usr/sbin/asterisk -rx \"rpt cmd " + myNode + " ilink 11 " + remoteNode + " \" \n";   //node to disconnect;
                            feedback = "Disconnecting from " + remoteNode;
                            break;
/*
                    case R.id.button3:
                        cmd = systemStatusCmd; // "sudo /usr/sbin/asterisk -rx \"rpt cmd "+ myNode + " ilink 5 "+ " \" \n";
                        feedback ="Status" + remoteNode;
                        break;
*/
                        case R.id.button4: //  node status
                            cmd = nodeStatusCmd;
                            nodeStatusFlag = true;
                            feedback = "Standby, getting stats";
                            break;

                        case R.id.button5: //  nodes connected
                            cmd = connectedNodesCmd;
                            connectedNodesFlag = true;
                            feedback = "Standby, getting list of connected nodes";
                            break;

                        default:
                            break;
                    }// switch
                    Context context = getApplicationContext();
                    CharSequence text = feedback;
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                // System.out.println("node cmd "+cmd);

                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        try {
                            StringBuilder sb = new StringBuilder();

                            //System.out.println("node cmd "+cmd);
                            results = node.executeRemoteCommand(source, user, passwd, host, port, cmd);

                            //reformat connected node list
                            if (connectedNodesFlag) {
                                connectedNodesFlag = false;
                                results = results.replace("*", "");
                                results = results.replace(", ", ",");
                                String[] tokens = results.split(",");

                                for (String t : tokens) {
                                    sb.append(t + "\n");
                                    //System.out.println(sb);
                                }
                                results = sb.toString();
                            }
                            if (nodeStatusFlag) {
                                results = results.replace("*", "");
                            }
                            if (connectFlag || disConnectFlag) {
                                disConnectFlag = false;
                                connectFlag = false;
                                results = node.executeRemoteCommand(source, user, passwd, host, port, connectedNodesCmd);
                                results = results.replace("*", "");
                                results = results.replace(", ", ",");
                                String[] tokens = results.split(",");

                                for (String t : tokens) {
                                    sb.append(t + "\n");
                                    //System.out.println(sb);
                                }
                                results = sb.toString();
                            }

                            Message msg = handler.obtainMessage();
                            msg.arg1 = 1;
                            handler.sendMessage(msg);

                            //System.out.println(results);
                        } catch (Exception e) {
                            //  System.out.println("Error with SSH connection");
                            Context context = getApplicationContext();
                            CharSequence text = "Error connecting to node";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);

            }
            else
            {
               // System.out.println("Error with settings, check your settings");
                Context context = getApplicationContext();
                CharSequence text = "Error with settings, check your settings";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }



    };

    private final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(results != null)
                if(msg.arg1 == 1)
                {
                    //Toast.makeText(getApplicationContext(), results, Toast.LENGTH_LONG).show();
                    CharSequence text = results;
                    textview.setText(text);
                }

        }

    };





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, UserSettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                System.out.println("Preferences");

                //showUserSettings();
                break;

        }

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // editor.putBoolean("silentMode", mSilentMode);

        // Commit the edits!
        editor.commit();

    }
}
