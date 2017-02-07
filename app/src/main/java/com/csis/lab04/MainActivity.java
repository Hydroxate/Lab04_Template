package com.csis.lab04; //package we're in


//android imports
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

//PURE DATA IMPORTS

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher; //must declare this to use later, used to receive data from sendEvents
    private SeekBar slider1; //Declaring slider1 here
    float slide1Value = 0.0f;

    TextView received1;
    TextView received2;
    TextView received3;
    TextView received4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//Mandatory
        setContentView(R.layout.activity_main);//Mandatory

        //For declaring and initialising XML items, Always of form OBJECT_TYPE VARIABLE_NAME = (OBJECT_TYPE) findViewById(R.id.ID_SPECIFIED_IN_XML);

        Button bang1 = (Button) findViewById(R.id.bang1); //findViewById uses the ids you specified in the xml!
        Button bang2 = (Button) findViewById(R.id.bang2); //findViewById uses the ids you specified in the xml!

        Button float1 = (Button) findViewById(R.id.float1); //findViewById uses the ids you specified in the xml!
        Button float2 = (Button) findViewById(R.id.float2); //findViewById uses the ids you specified in the xml!

        received1 = (TextView) findViewById(R.id.received1);
        received2 = (TextView) findViewById(R.id.received2);
        received3 = (TextView) findViewById(R.id.received3);
        received4 = (TextView) findViewById(R.id.received4);

        final EditText text1 = (EditText) findViewById(R.id.text1);
        final EditText text2 = (EditText) findViewById(R.id.text2);



        //Switch switch1 = (Switch) findViewById(R.id.switch1);//declared the switch here pointing to id onOffSwitch



        try { // try the code below, catch errors if things go wrong
            initPD(); //method is below to start PD
            loadPDPatch("synth.pd"); // This is the name of the patch in the zip
        } catch (IOException e) {
            e.printStackTrace(); // print error if init or load patch fails.
            finish(); // end program
        }

        //<------BUTTON CLICK LISTENER--------------->
        bang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("bang1");

            }
        });

        //<------BUTTON CLICK LISTENER--------------->
        bang2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendBangPD("bang2");

            }
        });

        //<------BUTTON CLICK LISTENER--------------->
        float1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("float1",Float.parseFloat(text1.getText().toString()));

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        //<------BUTTON CLICK LISTENER--------------->
        float2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("float2",Float.parseFloat(text2.getText().toString()));

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });

        //<--------SLIDER 1 LISTENER------------>
        slider1 = (SeekBar) findViewById(R.id.slider1);

        slider1.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        slide1Value = progress / 100.0f;

                        sendFloatPD("slider1", slide1Value);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });



    }

    @Override //If screen is resumed
    protected void onResume(){
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override//If we switch to other screen
    protected void onPause()
    {
        super.onPause();
        PdAudio.stopAudio();
    }

    //METHOD TO SEND FLOAT TO PUREDATA PATCH
    public void sendFloatPD(String receiver, Float value)//REQUIRES (RECEIVEEVENT NAME, FLOAT VALUE TO SEND)
    {
        PdBase.sendFloat(receiver, value); //send float to receiveEvent
    }

    //METHOD TO SEND BANG TO PUREDATA PATCH
    public void sendBangPD(String receiver)
    {

        PdBase.sendBang(receiver); //send bang to receiveEvent
    }

    private PdReceiver receiver1 = new PdReceiver() {

        private void pdPost(final String msg) {
            Log.e("RECEIVED:", msg);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void print(String s) {
            Log.i("PRINT",s);
            Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG);
        }

        @Override
        public void receiveBang(String source)
        {
            if(source.equals("send1"))
            {
                received1.setText("Bang: " + source);
            }
            else if(source.equals("send2"))
            {
                received2.setText("Bang: " + source);
            }
            else if(source.equals("send3"))
            {
                received3.setText("Bang: " + source);
            }
            else if(source.equals("send4"))
            {
                received4.setText("Bang: " + source);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Other ReceiveEvent called",Toast.LENGTH_LONG);
            }
            pdPost("bang");
        }

        @Override
        public void receiveFloat(String source, float x) {
            pdPost("float: " + x);
            if(source.equals("send1"))
            {
                received1.setText("Float: " + x);
            }
            else if(source.equals("send2"))
            {
                received2.setText("Float: " + x);
            }
            else if(source.equals("send3"))
            {
                received3.setText("Float: " + x);
            }
            else if(source.equals("send4"))
            {
                received4.setText("Float: " + x);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Other ReceiveEvent called",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void receiveList(String source, Object... args) {
            pdPost("list: " + Arrays.toString(args));

            if(source.equals("send1"))
            {
                received1.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send2"))
            {
                received2.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send3"))
            {
                received3.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send4"))
            {
                received4.setText("Message: " + Arrays.toString(args));
            }
            else
            {
                Toast.makeText(getBaseContext(),"Other ReceiveEvent called",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void receiveMessage(String source, String symbol, Object... args) {
            pdPost("message: " + Arrays.toString(args));

            if(source.equals("send1"))
            {
                received1.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send2"))
            {
                received2.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send3"))
            {
                received3.setText("Message: " + Arrays.toString(args));
            }
            else if(source.equals("send4"))
            {
                received4.setText("Message: " + Arrays.toString(args));
            }
            else
            {
                Toast.makeText(getBaseContext(),"Other ReceiveEvent called",Toast.LENGTH_LONG);
            }
        }

        @Override
        public void receiveSymbol(String source, String symbol) {
            pdPost("symbol: " + symbol);

            if(source.equals("send1"))
            {
                received1.setText("Symbol: " + symbol);
            }
            else if(source.equals("send2"))
            {
                received2.setText("Symbol: " + symbol);
            }
            else if(source.equals("send3"))
            {
                received3.setText("Symbol: " + symbol);
            }
            else if(source.equals("send4"))
            {
                received4.setText("symbol: " + symbol);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Other ReceiveEvent called",Toast.LENGTH_LONG);
            }
        }
    };


    //<---THIS METHOD LOADS SPECIFIED PATCH NAME----->
    private void loadPDPatch(String patchName) throws IOException
    {
        File dir = getFilesDir(); //Get current list of files in directory
        try {
            IoUtils.extractZipResource(getResources().openRawResource(R.raw.synth), dir, true); //extract the zip file in raw called synth
            File pdPatch = new File(dir, patchName); //Create file pointer to patch
            PdBase.openPatch(pdPatch.getAbsolutePath()); //open patch
        }catch (IOException e)
        {

        }
    }

    //<---THIS METHOD INITIALISES AUDIO SERVER----->
    private void initPD() throws IOException
    {
        int sampleRate = AudioParameters.suggestSampleRate(); //get sample rate from system
        PdAudio.initAudio(sampleRate,0,2,8,true); //initialise audio engine

        dispatcher = new PdUiDispatcher(); //create UI dispatcher
        PdBase.setReceiver(dispatcher); //set dispatcher to receive items from puredata patches

        dispatcher.addListener("send1",receiver1);
        PdBase.subscribe("send1");

        dispatcher.addListener("send2",receiver1);
        PdBase.subscribe("send2");

        dispatcher.addListener("send3",receiver1);
        PdBase.subscribe("send3");

        dispatcher.addListener("send4",receiver1);
        PdBase.subscribe("send4");
    }

}
