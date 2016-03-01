/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 12/8/14.
 */
public class BaseStructure {
    private static final Logger LOGGER = Logger.getLogger(BaseStructure.class);
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

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;
        try {
            List<String> newParams = new ArrayList<String>();

            for (String param : params) {
                if (param != null) {
                    newParams.add(param);
                }

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


}
