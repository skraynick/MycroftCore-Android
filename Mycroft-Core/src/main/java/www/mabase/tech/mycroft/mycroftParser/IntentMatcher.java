package www.mabase.tech.mycroft.mycroftParser;

import android.util.Log;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;

/**
 * Currently, this is specifically training a SimpleRNN based on the needs of intent classification,
 * but it could be instead used to tie to a parameter interface in the app settings, and allow for
 * multiple models to be trained here. Then I can offload a couple of default NN for use by general
 * users
 */

public class IntentMatcher {

    //Whens should I initialize the intent parser and it's model? Should it be a service
    //That is running w/ core, and then is called here? or is it inexspenive enough to implement
    //and load the model each time (I'm inclined to keep it always loaded as a service)

    /**
     *For real receiveUtterance is handled by ParseIntentReceiver. I need to merge the two
     */
    //This is where the service will receive the utterance
    void receiveUtterance(String utterance){
        TextDataCleaner inputCleaner = new TextDataCleaner();
        inputCleaner.prepareUtterance(utterance); //This defined in TextDataCleaner,java
    }

    double[] matchUtterance(double prepared[]){
        //Load prepared data into NN, and match utterance

        MultiLayerNetwork myNetwork = null;

        //The utterance had been matched to a dictionary/index for vectorization
        File savedNetwork = null;
        INDArray actualInput = Nd4j.create(prepared);

        try {
            myNetwork = ModelSerializer.restoreMultiLayerNetwork(savedNetwork);
        }catch (Exception p){
            //Damn, it wasn't there
            //default to a training prompt?
        }
        INDArray actualOutput = myNetwork.output(actualInput);
        Log.d("myNetwork Output ", actualOutput.toString());


        double[] topFive = null; //returned value
        return topFive;
    }

    void skillPicker(double[] topFive){
        //This can be extended to decide between multiple responses, using context or multi
        //parsers. For now, just use the first one by default
        //Intent skill = new Intent(topFive[0]);

        //Technically the NN is already doing this step, but there might be something more complex
        //I want to implement
    }

    void saveResponse(){
        //This logs the skill that was picked, so that the user can correct it if it's worng
        //and the dataset can be increased as time goes on
    }
}
