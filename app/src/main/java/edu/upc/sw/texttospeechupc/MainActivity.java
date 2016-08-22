package edu.upc.sw.texttospeechupc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ServiceConfigurationError;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    TextToSpeech tts;
    int result;
    EditText et;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et = (EditText) findViewById(R.id.editText);
        //Este metodo selecciona el codigo del lenguaje y lo coloca en result como un entero
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener(){

            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                    //result = tts.setLanguage(Locale.US);

                }
                else{
                    Toast.makeText(getApplicationContext(), "Feature not Supported in your device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner3);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_spinner, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.pitch_Spinner, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.speed_Spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

        Spinner spinner = (Spinner) parent;

        if(spinner.getId() == R.id.spinner)
        {
            String lang = parent.getItemAtPosition(pos).toString();

            // Setting up a Toast to ensure the handling
            Toast.makeText(this, lang + " Selected", Toast.LENGTH_LONG).show();

            if(lang.equalsIgnoreCase("English"))
            {
                tts.setLanguage(Locale.US);
            }
            else if(lang.equalsIgnoreCase("Spanish"))
            {
                tts.setLanguage(new Locale("es","MX"));
            }

        }
        // This code is to set the pitch of voice
        if(spinner.getId() == R.id.spinner2)
        {
            String pitch = spinner.getItemAtPosition(pos).toString();
            if(pitch.equalsIgnoreCase("Higher"))
            {
                tts.setPitch((float) 2.0);
            }
            else if(pitch.equalsIgnoreCase("Lower"))
            {
                tts.setPitch((float)0.5);
            }
            else{
                tts.setPitch((float)1);
            }
        }
        // This code is to set the Speech rate of voice
        else if(spinner.getId() == R.id.spinner3)
        {
            String speed = spinner.getItemAtPosition(pos).toString();
            if(speed.equalsIgnoreCase("High"))
            {
                tts.setSpeechRate((float) 2.0);
            }
            else if(speed.equalsIgnoreCase("Low"))
            {
                tts.setSpeechRate((float)0.5);
            }
            else{
                tts.setSpeechRate((float)1);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void doSomething(View v){
        switch(v.getId()) {
            case R.id.bspeak:
                if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                    Toast.makeText(getApplicationContext(), "Feature not Supported in your device", Toast.LENGTH_SHORT).show();
                }
                else{
                    text = et.getText().toString();

                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                break;

            case R.id.bstopspeaking:
                if(tts != null){
                    tts.stop();
                }
                break;
        }
    }

    public void exceptSpeechInput(View v) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_input_phrase));

        try {

            startActivityForResult(intent, 100);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.stt_not_supported), Toast.LENGTH_LONG).show();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if((requestCode == 100) && (data != null) && (resultCode == RESULT_OK)){

            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            EditText wordsEntered = (EditText) findViewById(R.id.editText);

            wordsEntered.setText(spokenText.get(0));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                //startService(new Intent(Settings.Secure.TTS_DEFAULT_SYNTH));
                startActivityForResult(new Intent(Settings.ACTION_SEARCH_SETTINGS), 0);
            }
            catch (ActivityNotFoundException ex){
                ex.printStackTrace();
            }
        }

        if(id == R.id.acerca_de){
            Intent intent =  new Intent(MainActivity.this, AcercaDe.class);
            MainActivity.this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

    }

}
