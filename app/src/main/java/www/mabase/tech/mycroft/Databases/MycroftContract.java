package www.mabase.tech.mycroft.Databases;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Chris on 10/25/2017.
 */

public class MycroftContract {

    public static final String CONTENT_AUTHORITY = "www.mabase.tech.mycroft";

    //The content authority is used to create the base of all URIs which apps will use to contact
    //this content provider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    //A list of possible paths that will be appended to the base URI for each of the different tables
    public static final String PATH_ENTITY = "entity";
    public static final String PATH_INTENT = "intent";
    public static final String PATH_MODULE = "module";

    private MycroftContract() {}

    public static final class Entities implements BaseColumns {
        public static final String TABLE_NAME = "entities";

        //This is the value of the word, words, regex, or phrase to be used for tagging, etc
        public static final String COLUMN_NAME_STRING = "string";
        //This tells if it is a word, words, regex, or phrase, for parsers to load the database
        public static final String COLUMN_NAME_TYPE = "type";
        //Tag refers to how a word should be tagged with the entity tagger. This also corresponds
        //to the .voc filename
        public static final String COLUMN_NAME_TAG = "tag";
        //Skill is the name of the app or service to be interacted with
        public static final String COLUMN_NAME_SKILL = "skill";
    }

    //This table handles the breakdown for every intent (or independent task) available in a skill
    //or app. It lists what the action is, and what variables are needed or optional
    public static final class Intents implements BaseColumns {
        //This is the table name
        public static final String TABLE_NAME = "intents";

        //Skill is the name of the app or service to be interacted with
        public static final String COLUMN_NAME_SKILL = "skill";
        //This is the particular action or actions in the app/service that is going to be run
        public static final String COLUMN_NAME_INTENT = "intent";
        //Value refers to whether it's needed, can have multiple, or is optional
        public static final String COLUMN_NAME_VALUE = "value";
        //Tag refers to which tag value of word is needed to fill this slot
        public static final String COLUMN_NAME_TAG = "tag";
    }

    public static final class Module implements BaseColumns{
        //This is the table name
        public static final String TABLE_NAME = "modules";

        //This is the manifest identifier of a skill that works with this parser
        public static final String COLUMN_NAME_SKILL = "skill";
        //This is the package name of the module
        public static final String COLUMN_NAME_PACKAGE = "package";
        //This is the type of module (parser, chatbot, etc)
        public static final String COLUMN_NAME_TYPE = "type";
    }
}
