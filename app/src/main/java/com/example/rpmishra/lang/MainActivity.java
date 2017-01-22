package com.example.rpmishra.lang;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.prediction.Concept;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import java.util.*;


public class MainActivity extends AppCompatActivity {
    Button mButton;
    Button photoButton;
    EditText mEdit;
    TextView mText;
    public static final String EXTRA_MESSAGE = "MOTHERTONGUE";
    public static final String LABELS = "LABELS";
    public static final int  CAMERA_PIC_REQUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





            mButton = (Button)findViewById(R.id.buttonSave);

            mButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                // call image upload activity with intent
                    Intent intent = new Intent(getApplicationContext(), DummyImageCaptureActivity.class);
                    EditText editText = (EditText) findViewById(R.id.editTextTranslation);
                    String message = editText.getText().toString();

                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);


                }
            });

        photoButton = (Button)findViewById(R.id.buttonPhoto);

        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent,CAMERA_PIC_REQUEST);


            }
        });





    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final ClarifaiClient client = new ClarifaiBuilder("OdAnq0ddscI7PZMMdoNykS8iDG2nHhulIXSjc2-7", "4lOLUgjHBl0MqZAZly_nO0ANeVZfTjeYJ6qN0P9G").buildSync();
        final byte[] imageBytes = ClarifaiUtil.retrieveSelectedImage(this, data);
        final List<ClarifaiOutput<Concept>> predictionResults;
        if (requestCode == CAMERA_PIC_REQUEST) {

           predictionResults =
                    client.getDefaultModels().generalModel() // You can also do client.getModelByID("id") to get custom models
                            .predict()
                            .withInputs(
                                    ClarifaiInput.forImage(ClarifaiImage.of(imageBytes)))

                            .executeSync()
                            .get();

            if(!predictionResults.isEmpty()){
                showLabels(predictionResults);
            }
        }





    }


    private void showLabels(List<ClarifaiOutput<Concept>> predictionResults){
        Intent intent = new Intent(getApplicationContext(), DisplayLabelActivity.class);
        StringBuffer sb = new StringBuffer();
        String[] arr = new String[predictionResults.size()];

        for (int i = 0; i < predictionResults.size(); i++) {

            ClarifaiOutput<Concept> clarifaiOutput = predictionResults.get(i);

            List<Concept> concepts = clarifaiOutput.data();


            if(concepts != null && concepts.size() > 0) {
                for (int j = 0; j < concepts.size(); j++) {
                    arr[j] = concepts.get(j).name();
                }
            }
        }
        intent.putExtra(LABELS, arr);
        startActivity(intent);
    }



}
