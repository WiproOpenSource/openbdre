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

package com.wipro.ats.bdre.im.etl.api.sftp;

import com.jcraft.jsch.UserInfo;
import org.apache.log4j.Logger;

import java.io.Console;

/**
 * Created by arijit on 12/30/14.
 */
public class FTPUser implements UserInfo {
    private static final Logger LOGGER = Logger.getLogger(FTPUser.class);
    private String passphrase;
    private String password;

    public FTPUser(String password) {
        this.password = password;
    }

    @Override
    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean promptPassword(String s) {
        if (password == null) {
            Console console = System.console();
            char[] pwd = console.readPassword("Enter password: ");
            password = new String(pwd);
        }
        return true;
    }

    @Override
    public boolean promptPassphrase(String s) {
        return true;
    }

    @Override
    public boolean promptYesNo(String s) {
        try {

            //TODO: Implement a common location of the trust file
            return true;

        } catch (Exception e) {
            LOGGER.error(e);
            return false;
        }


    }

    @Override
    public void showMessage(String s) {
        LOGGER.info("s = " + s);

    }


}
