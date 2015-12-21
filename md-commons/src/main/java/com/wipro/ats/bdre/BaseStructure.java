/*
 * Copyright 2015 Wipro Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by arijit on 12/8/14.
 */
public class BaseStructure {
    private static Logger LOGGER = Logger.getLogger(BaseStructure.class);
    private static final int PRINT_USAGE_WIDTH = 80;
    private static final int DESCPAD = 5;
    private static final int LEFTPAD = 10;
    private static final int PRINT_OPTION_WIDTH = 100;

    /**
     * <p>Prints the usage statement for the specified application.</p>
     *
     * @param applicationName The application name
     * @param errorMsg        error message to print
     * @param options         The command line Options
     * @param out             instance of OutputStream
     */
    public final void printUsage(
            String applicationName,
            String errorMsg,
            Options options,
            OutputStream out

    ) {
        final PrintWriter writer = new PrintWriter(out);
        final HelpFormatter usageFormatter = new HelpFormatter();
        writer.println(errorMsg);
        usageFormatter.printUsage(writer, PRINT_USAGE_WIDTH, applicationName, options);
        usageFormatter.printOptions(writer, PRINT_OPTION_WIDTH, options, LEFTPAD, DESCPAD);
        writer.close();
    }

    /**
     * This method reads command line arguments
     *
     * @param params         It is a string array having maximum batch, environment variable and process-id
     *                       with their commandline notion
     * @param paramStructure two dimensional array for developing options for commandline argument
     * @return
     */

    public final CommandLine getCommandLine(String[] params, String[][] paramStructure) {
        final Options options = new Options();
        // iterating over paramStructure two dimensional Array
        for (String[] optionStrings : paramStructure) {
            Option a = new Option(optionStrings[0], optionStrings[1], true, optionStrings[2]);
            a.setRequired(true);
            options.addOption(a);
        }

        Option env = new Option("env", "environment-id", true, "Environment Id(dev, local, test, production etc");
        env.setOptionalArg(true);
        env.setRequired(false);
        options.addOption(env);

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            //This will take care of null environment value. Basically get rid of the null value if  -env followed by null
            ArrayList<String> newParams = new ArrayList<String>();
            String strEnv = params[params.length - 1];
            if (strEnv != null && (strEnv.equals("-env") || strEnv.equals("--environment-id"))) {
                newParams.add("--environment-id");
                newParams.add(getDefaultEnv());
                LOGGER.debug("Last element is " + strEnv + " hence no env values, therefore adding --environment-id:" + getDefaultEnv());
            }
            boolean envParamExists = false;
            for (String param : params) {
                if (param != null) {
                    newParams.add(param);
                    if ((param.equals("-env") || param.equals("--environment-id"))) {
                        envParamExists = true;
                    }
                } else {
                    LOGGER.debug("env arg is null. therefore adding --environment-id:" + getDefaultEnv());
                    newParams.add(getDefaultEnv());
                }

            }
            if (!envParamExists) {
                newParams.add("--environment-id");
                newParams.add(getDefaultEnv());
                LOGGER.debug("-env param is not supplied, therefore adding --environment-id:" + getDefaultEnv());
            }
            cmd = parser.parse(options, newParams.toArray(new String[]{}));
            return cmd;

        } catch (ParseException e) {
            //System.err cannot be removed as it is an command line argument
            printUsage("java <main_class> ", e.getMessage(), options, System.err);
            System.exit(1);
        }
        return null;
    }

    //This is to add default option support for environment Option
    private String defaultEnv = null;

    protected String getDefaultEnv() {
        if (defaultEnv != null) {
            return defaultEnv;
        }
        Properties properties = new Properties();
        try {

            properties.load(BaseStructure.class.getResourceAsStream("/ENVIRONMENT"));
            defaultEnv = properties.getProperty("environment");

        } catch (IOException e) {
            LOGGER.error("Please create ENVIRONMENT file in resources folder of md-commons project with content 'environment=env1' in it if env1 is your environment");

        }
        return defaultEnv;
    }
}
