package tech.mabase.www.adapt;

import org.w3c.dom.Text;

import tech.mabase.www.NNIntentParser.TextDataCleaner;

public class IntentMatcher {

    //Whens should I initialize the intent parser and it's model? Should it be a service
    //That is running w/ core, and then is called here? or is it inexspenive enough to implement
    //and load the model each time (I'm inclined to keep it always loaded as a service)

    /**
     *For real receiveUtterance is handleed by ParseIntentReceiver. I need to merge the two
     */
    //This is where the service will receive the utterance
    void receiveUtterance(String utterance){
        TextDataCleaner inputCleaner = new TextDataCleaner();
        inputCleaner.prepareUtterance(utterance); //This defined in TextDataCleaner,java
    }

    double[] matchUtterance(double prepared[]){
        //Load prepared data into NN, and match utterance
        double[] topFive = null; //returned value
        return topFive;
    }

    void skillPicker(double[] topFive){
        //This can be extended to decide between multiple responses, using context or multi
        //parsers. For now, just use the first one by default
        //Intent skill = new Intent(topFive[0]);
    }

    void saveResponse(){
        //This logs the skill that was picked, so that the user can correct it if it's worng
        //and the dataset can be increased as time goes on
    }
}
