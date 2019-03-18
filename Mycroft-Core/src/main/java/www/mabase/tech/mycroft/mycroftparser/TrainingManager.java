package www.mabase.tech.mycroft.mycroftparser;

        import android.os.AsyncTask;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
        import org.deeplearning4j.nn.conf.layers.DenseLayer;
        import org.deeplearning4j.nn.conf.layers.OutputLayer;
        import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
        import org.deeplearning4j.nn.weights.WeightInit;
        import org.nd4j.linalg.activations.Activation;
        import org.nd4j.linalg.api.ndarray.INDArray;
        import org.nd4j.linalg.dataset.DataSet;
        import org.nd4j.linalg.factory.Nd4j;
        import org.nd4j.linalg.lossfunctions.LossFunctions;

        import java.util.Arrays;

public class TrainingManager extends AppCompatActivity {

    //Global variables to accept the classification results from the background thread.
    //May need to be dynamically allocated
    double first;
    double second;
    double third;
    //Instead, make an array that equals the numebr of intents saved in the database
    //double categories = getNumOfIntents();

    /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //onclick to capture the input
        final EditText PL = (EditText) findViewById(R.id.editText);
        final EditText PW = (EditText) findViewById(R.id.editText2);
        final EditText SL = (EditText) findViewById(R.id.editText3);
        final EditText SW = (EditText) findViewById(R.id.editText4);

        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final double pl = Double.parseDouble(PL.getText().toString());
                final double pw = Double.parseDouble(PW.getText().toString());
                final double sl = Double.parseDouble(SL.getText().toString());
                final double sw = Double.parseDouble(SW.getText().toString());

                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute(pl,pw,sl,sw);
                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
                bar.setVisibility(View.VISIBLE);
            }
        });
    }
    */

    private class AsyncTaskRunner extends AsyncTask<Double, Integer, String> {
        /*
        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);
        }
        */

        // numOfSampleInputs = getNumFromDatabase()
        int numOfSampleInputs = 0;
        // maxInputLength = maxInputLengthInDatabase()
        int maxInputLength = 0;
        //These aren't going to be ints/doubles. It's just temp
        double[] sampleFileStream = null;
        double[] intentLabels = null;

        /* Aha! Here is where PL and such are defined by user input
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //onclick to capture the input
            final EditText PL = (EditText) findViewById(R.id.editText);
            final EditText PW = (EditText) findViewById(R.id.editText2);
            final EditText SL = (EditText) findViewById(R.id.editText3);
            final EditText SW = (EditText) findViewById(R.id.editText4);

            Button button = (Button) findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final double pl = Double.parseDouble(PL.getText().toString());
                    final double pw = Double.parseDouble(PW.getText().toString());
                    final double sl = Double.parseDouble(SL.getText().toString());
                    final double sw = Double.parseDouble(SW.getText().toString());

                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute(pl,pw,sl,sw);
                    ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
                    bar.setVisibility(View.VISIBLE);
                }
            });
        }
        */


        // This is our main background thread for the neural net
        @Override
        protected String doInBackground(Double... params) {

            //Get the doubles from params, which is an array so they will be 0,1,2,3
            //Each of these corrispond to one of the 4 parameters for the iris. What are the
            //parameters for intents?
            double pld = params[0];
            double pwd = params[1];
            double sld = params[2];
            double swd = params[3];

            //This should be String utterance = input, and then it should be tokenized and matched
            //with the internal word dictionary


            //Write them in the log
            Log.d("myNetwork Output ", "do in background string pl = " + pld);
            Log.d("myNetwork Output ", "do in background string pw = " + pwd);
            Log.d("myNetwork Output ", "do in background string sl = " + sld);
            Log.d("myNetwork Output ", "do in background string sw = " + swd);


            //Create input
            //This is the input, It takes the 4 params defined as doubles above. I think its user defined
            INDArray actualInput = Nd4j.zeros(1,4);
            actualInput.putScalar(new int[]{0,0}, pld);
            actualInput.putScalar(new int[]{0,1}, pwd);
            actualInput.putScalar(new int[]{0,2}, sld);
            actualInput.putScalar(new int[]{0,3}, swd);

            //Convert the iris data into 150x4 matrix
            //The row corresponds to the number of sample inputs, the col corresponds to the number
            //of features. for me, the number of features is the number of tokens in the largest
            //example input.
            /*
            int row=150
            int col=4
             */
            int row=numOfSampleInputs;
            int col=maxInputLength;

            //This is for the flower parameters
            /*
            double[][] irisMatrix=new double[row][col];
            int i = 0;
            for(int r=0; r<row; r++){
                for( int c=0; c<col; c++){
                    irisMatrix[r][c]= org.deeplearning4j.examples.iris_classifier.DataSet.irisData[i++];
                }
            }
            */

            //This is my input matrix for the sample input sentences.
            //Something needs to clean up the data before loading it in here
            double[][] intentMatrix = new double[row][col];
            int i = 0;
            for(int r=0; r<row; r++){
                for(int c=0; c<col; c++){
                    //This is likely to call the data from the database. When should I clean it?
                    //When installed (how would I save it in the database?), when initilized? on each
                    //run?
                    intentMatrix[r][c] = sampleFileStream[i++];
                }
            }

            //Check the array by printing it in the log
            System.out.println(Arrays.deepToString(intentMatrix).replace("], ", "]\n"));

            //Now do the same for the label data
            //This is going to be the intent data. Rows = num of utterance examples, col = possible intents
            int rowLabel=150;
            //This is the number of possible labels in the database
            int colLabel=3;

            //This is for the label
            double[][] twodimLabel=new double[rowLabel][colLabel];
            int ii = 0;
            for(int r=0; r<rowLabel; r++){
                for( int c=0; c<colLabel; c++){
                    twodimLabel[r][c]= intentLabels[ii++];
                }
            }

            System.out.println(Arrays.deepToString(twodimLabel).replace("], ", "]\n"));

            //Convert the data matrices into training INDArrays
            //THIS IS WHERE THE TRAINING SETS ARE DEFINED
            INDArray trainingIn = Nd4j.create(intentMatrix); //Change this to a mapped in matrix
            INDArray trainingOut = Nd4j.create(twodimLabel);

            //build the layers of the network.
            //BUILDER SECTION. THIS IS WHERE IS BUILD THE RNN LAYERS.
            /* Original code
            DenseLayer inputLayer = new DenseLayer.Builder()
                    .nIn(4)
                    .nOut(3)
                    .name("Input")
                    .build();
            */

            //This is the NN that is being built
            SimpleRnn inputLayer = new SimpleRnn.Builder()
                    //This will be the number of input parameters
                    .nIn(4) //<- .nIn(lengthOfLongestSentence)
                    //I think this will be the number of intent categories
                    .nOut(3) //<- .nOut(numOfIntents)
                    .name("Input")
                    .build();
            /* original hidden layer
            DenseLayer hiddenLayer = new DenseLayer.Builder()
                    .nIn(3)
                    .nOut(3)
                    .name("Hidden")
                    .build();
             */
            SimpleRnn hiddenLayer = new SimpleRnn.Builder()
                    //I believe this is 3 because it is supposed to match the output of inputLayer
                    .nIn(3) // Longest sentence /2 (this can't change each training time though... can it?)
                    .nOut(3) //Do they need to be equal
                    .name("Hidden")
                    .build();

            /* original output layer
            OutputLayer outputLayer = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .nIn(3)
                    .nOut(3)
                    .name("Output")
                    .activation(Activation.SOFTMAX)
                    .build();
            */
            SimpleRnn outputLayer = new SimpleRnn.Builder()
                    //Same as the hiddenLayer, match the input to hiddenLayers output
                    .nIn(3)  //I think this needs to match the output of the prior layer, so it's full connected
                    .nOut(3) //numOfIntents
                    .name("Output")
                    .build();

            //I think I can leave this alone
            NeuralNetConfiguration.Builder nncBuilder = new NeuralNetConfiguration.Builder();
            long seed = 6;
            nncBuilder.seed(seed);
            nncBuilder.activation(Activation.TANH);
            nncBuilder.weightInit(WeightInit.XAVIER);

            //This is each layer as defined upp in the builder section
            NeuralNetConfiguration.ListBuilder listBuilder = nncBuilder.list();
            listBuilder.layer(0, inputLayer);
            listBuilder.layer(1, hiddenLayer);
            listBuilder.layer(2, outputLayer);

            listBuilder.backprop(true);

            //Ah! It's a multilayer network (See: bulder section), so that'll be how I save it
            MultiLayerNetwork myNetwork = new MultiLayerNetwork(listBuilder.build());
            myNetwork.init();


            //Create a data set from the INDArrays and train the network
            //THIS IS WHERE I LOAD THE CUSTOM DATA FROM THE CORE DATABASE
            DataSet myData = new DataSet(trainingIn, trainingOut);
            for(int l=0; l<=1000; l++) {
                //I think I need to change this for specific features of RNN
                //myNetwork.rnnTimeStep();
                //myNetwork.rnnSetPreviousState();
                myNetwork.fit(myData);
            }

            //Evaluate the input data against the model
            INDArray actualOutput = myNetwork.output(actualInput);
            Log.d("myNetwork Output ", actualOutput.toString());

            //Retrieve the three probabilities
            //This could be set up as a dynamic array, so it adjusts w/ the number of intents

            /*
            for(int i = 0; i < categories.length(); i++){
                categories[0,i] = actualOutput.getDouble(0, i);

                return "":
            }
            */

            first = actualOutput.getDouble(0,0);
            second = actualOutput.getDouble(0,1);
            third = actualOutput.getDouble(0,2);

            //Since we used global variables to store the classification results, no need to return
            //a results string. If the results were returned here they would be passed to onPostExecute.

            return "";
        }

        //This is called from background thread but runs in UI for a progress indicator
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /*
        //This block executes in UI when background thread finishes
        //This is where we update the UI with our classification results
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //Hide the progress bar now that we are finished
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);

            //Update the UI with output

            TextView setosa = (TextView) findViewById(R.id.textView11);
            TextView versicolor = (TextView) findViewById(R.id.textView12);
            TextView virginica = (TextView) findViewById(R.id.textView13);

            //Limit the double to values to two decimals using DecimalFormat
            DecimalFormat df2 = new DecimalFormat(".##");

            setosa.setText(String.valueOf(df2.format(first)));
            versicolor.setText(String.valueOf(df2.format(second)));
            virginica.setText(String.valueOf(df2.format(third)));

        }
        */

        //Somewhere down here I should add a save and load feature, so that I can improve upon
        //an existing network rather than doing it from scratch each time
    }
}