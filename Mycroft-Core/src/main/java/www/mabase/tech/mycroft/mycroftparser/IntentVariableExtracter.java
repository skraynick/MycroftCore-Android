package www.mabase.tech.mycroft.mycroftparser;

public class IntentVariableExtracter {
    /**
     * This is going to use a NN to extract the variables needed to execute the intent, and then
     * send them along to said intent as a serialized message.
     *
     * I expect to tokenize the sentence and match it against a secondary NN that has individually
     * tagged each intent item. Hopefully this will account for unknown entities over time, however
     * I will need to save a model for each skill, which means training time for it. Perhaps
     * eventually the training can be done by a SDK, but I may need to figure out how to do it here
     * low impact until then. That said, it could be that they all used transfer NNs instead of
     * needing to do it from scratch each time.
     */
}
