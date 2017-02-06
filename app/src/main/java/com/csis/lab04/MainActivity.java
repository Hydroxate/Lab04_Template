package com.csis.lab04; //package we're in


//android imports
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

//PURE DATA IMPORTS

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private PdUiDispatcher dispatcher; //must declare this to use later, used to receive data from sendEvents
    private SeekBar slider1; //Declaring slider1 here
    float slide1Value = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//Mandatory
        setContentView(R.layout.activity_main);//Mandatory

        //For declaring and initialising XML items, Always of form OBJECT_TYPE VARIABLE_NAME = (OBJECT_TYPE) findViewById(R.id.ID_SPECIFIED_IN_XML);

        Button bang1 = (Button) findViewById(R.id.bang1); //findViewById uses the ids you specified in the xml!
        Button bang2 = (Button) findViewById(R.id.bang2); //findViewById uses the ids you specified in the xml!

        Button float1 = (Button) findViewById(R.id.float1); //findViewById uses the ids you specified in the xml!
        Button float2 = (Button) findViewById(R.id.float2); //findViewById uses the ids you specified in the xml!

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

            }
        });

        //<------BUTTON CLICK LISTENER--------------->
        float2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendFloatPD("float2",Float.parseFloat(text2.getText().toString()));

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
    }

}
