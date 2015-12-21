/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
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
            /*String footPrint = FTPUtil.extractFootPrint(s);

            if ((footPrint != null || !footPrint.isEmpty()) && FTPUtil.getKnownFootPrints().contains(footPrint)) {
                return true;
            }
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            System.out.print(s);
            System.out.print(" (y/n): ");
            String response = bufferRead.readLine();

            if (response == null || response.isEmpty()) {
                return promptYesNo(s);
            } else if (response.equalsIgnoreCase("y")) {

                LOGGER.info("adding " + footPrint);
                FTPUtil.addToKnownFootPrints(footPrint);

                return true;
            } else {
                return false;
            }*/
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
