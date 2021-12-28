package com.dktechhub.morsecode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    ImageButton play,audio,flash,toogleButton;
    Button convertButton;
    EditText input;
    TextView output,m1,m2;
    boolean encodeMode = false;
    private Camera mCamera;
    private Parameters parameters;
    private CameraManager camManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        McodeConverter mcodeConverter = new McodeConverter();
        input = findViewById(R.id.input);
        output= findViewById(R.id.output);
        m1 = findViewById(R.id.m1);
        m2= findViewById(R.id.m2);
        toogleButton=findViewById(R.id.toggle_button);
        toogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMode(false);
            }
        });

        convertButton= findViewById(R.id.convert_button);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(encodeMode)
                    output.setText(mcodeConverter.encodeUSA(input.getText().toString()));
                else output.setText(mcodeConverter.decodeUSA(input.getText().toString()));
            }
        });

        play=findViewById(R.id.play_button);
        audio=findViewById(R.id.audio_button);
        flash=findViewById(R.id.flash);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    convertButton.setEnabled(true);
                }else convertButton.setEnabled(false);

                if(editable.toString().contains("_")||editable.toString().contains("."))
                    toggleMode(true);

                else {
                    toggleMode(true);
                    toggleMode(false);
                }
            }
        });


        output.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0)
                {
                    play.setEnabled(true);
                   audio.setEnabled(true);flash.setEnabled(true);
                }else {
                    play.setEnabled(false);
                    audio.setEnabled(false);flash.setEnabled(false);
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard();
            }
        });

        toggleMode(false);

        initFlashLight(false);

        initAudio(false);

        //setAudio(true);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playingFlash)
                {
                    playingFlash=false;
                    flash.setImageResource(R.drawable.ic_baseline_flash_on_24);
                }
                else {
                    playOutputFlash(output.getText().toString());
                    flash.setImageResource(R.drawable.ic_baseline_flash_off_24);
                }
            }
        });

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playingAudio)
                {
                    playingAudio=false;
                    audio.setImageResource(R.drawable.ic_baseline_volume_up_24);
                }else {
                    audio.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    playAudioOutput(output.getText().toString());
                }
            }
        });

    }

    public void toggleMode(boolean forceDecode)
    {
        encodeMode=(!encodeMode);
        if(forceDecode)
            encodeMode=false;
        if(encodeMode)
        {
            m1.setText(getString(R.string.english));
            m2.setText(getString(R.string.morse));
        }else {
            m1.setText(getString(R.string.morse));
            m2.setText(getString(R.string.english));
        }
    }

    public void copyToClipboard()
    {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.generatedtext),output.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, getString(R.string.textcopied), Toast.LENGTH_SHORT).show();
    }


    public void setFlashLight(boolean state)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            try {
                camManager.setTorchMode(cameraId,state);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }else {
            if(state)
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            else parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        }
    }
    String cameraId;
    public void initFlashLight(boolean dismiss)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                cameraId = null;
                if (camManager != null) {
                    cameraId = camManager.getCameraIdList()[0];
                    //camManager.setTorchMode(cameraId, true);
                }
            } catch (CameraAccessException e) {
                //Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        } else {
            mCamera = Camera.open();
            parameters = mCamera.getParameters();
            if(dismiss)
                mCamera.stopPreview();
            //parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            //mCamera.setParameters(parameters);
            //mCamera.startPreview();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initFlashLight(true);
        initAudio(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        initFlashLight(true);
        initAudio(true);
    }

    private boolean playingFlash = false;

    public void playOutputFlash(String morsed)
    {   Log.d("Flash","Initilising");
        if(playingFlash)
        {
            Toast.makeText(this, "Already playing flashlight output", Toast.LENGTH_SHORT).show();
            return;
        }
        Thread thread = new Thread(() -> {
            try{
                setFlashLight(false);
                playingFlash=true;
                for(int i=0;i<morsed.length()&&playingFlash;i++)
                {
                    char c = morsed.charAt(i);
                    if(c==' ')
                    {
                        setFlashLight(false);
                        Thread.sleep(McodeConverter.UNIT);
                    }else if(c=='.')
                    {
                        setFlashLight(true);
                        Thread.sleep(McodeConverter.DOT);
                    }else if(c=='_')
                    {
                        setFlashLight(true);
                        Thread.sleep(McodeConverter.DASH);
                    }
                }

                setFlashLight(false);
                playingFlash=false;
                flash.setImageResource(R.drawable.ic_baseline_flash_on_24);
                Log.d("Flash","Finished");
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        thread.start();
    }
    AudioManager audioManager;
    ToneGenerator toneGenerator;
    public void setAudio(int dur)
    {

        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP,dur);

    }

    public void initAudio(boolean dismiss)
    {   if(!dismiss) {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }
    else toneGenerator.release();

    }

    private boolean playingAudio = false;
    public void playAudioOutput(String morsed)
    {
        if(playingAudio)
        {
            Toast.makeText(this, "Already playing audio output", Toast.LENGTH_SHORT).show();
            return;
        }

        Thread thread = new Thread(() -> {
            try{
                //setAudio(false);
                playingAudio=true;

                for(int i=0;i<morsed.length()&&playingAudio;i++)
                {
                    char c = morsed.charAt(i);
                    if(c==' ')
                    {
                        //setAudio(McodeConverter.UNIT);
                        Thread.sleep(McodeConverter.UNIT*2);
                    }else if(c=='.')
                    {
                        setAudio(McodeConverter.DOT*2);
                        Thread.sleep(McodeConverter.DOT*2);
                    }else if(c=='_')
                    {
                        setAudio(McodeConverter.DASH*2);
                        Thread.sleep(McodeConverter.DASH*2);
                    }
                }


                //setAudio(false);
                playingAudio=false;
                audio.setImageResource(R.drawable.ic_baseline_volume_up_24);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        thread.start();
    }

}