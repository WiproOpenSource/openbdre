/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.im.etl.api.sftp;

import com.jcraft.jsch.*;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.md.beans.FileInfo;

import java.io.*;

/**
 * Created by arijit on 12/30/14.
 */
public class SFTP {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SFTP.class);

    public void download(FileInfo fileInfo, String destination) {


        String user = fileInfo.getUsername();
        String pwd = fileInfo.getPassword();
        String host = fileInfo.getServerIP() == null ? fileInfo.getServerIP() : fileInfo.getServerName();
        String port = "22";
        if (host.indexOf(":") != -1) {
            host = host.split(":")[0];
            port = host.split(":")[1];
        }
        String key = fileInfo.getSshPrivateKey();
        String rfile = fileInfo.getFilePath();
        String lfile = destination;
        Session session = null;

        if (key != null && !key.isEmpty()) {
            session = connectedKeyBasedSFTPSession(user, host, new Integer(port), key);
        } else {
            session = connectedPasswordBasedSFTPSession(user, host, new Integer(port), pwd);
        }
        transferFile(session, rfile, destination);
    }


    private Session connectedPasswordBasedSFTPSession(String user, String host, int port, String password) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            // username and password will be given via UserInfo interface.
            UserInfo ui = new FTPUser(password);
            session.setUserInfo(ui);
            session.connect();
            return session;
        } catch (JSchException e) {
            LOGGER.error(e);
            throw new ETLException(e);
        }
    }

    private Session connectedKeyBasedSFTPSession(String user, String host, int port, String key) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(key);
            Session session = jsch.getSession(user, host, port);
            session.connect();
            return session;
        } catch (JSchException e) {
            LOGGER.error(e);
            throw new ETLException(e);
        }
    }

    private void transferFile(Session session, String remoteFile, String localFile) {
        {
            FileOutputStream fos = null;
            try {
                String prefix = null;
                if (new File(localFile).isDirectory()) {
                    prefix = localFile + File.separator;
                }
                // exec 'scp -f rfile' remotely
                String command = "scp -f " + remoteFile;
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                // get I/O streams for remote scp
                OutputStream out = channel.getOutputStream();
                InputStream in = channel.getInputStream();
                channel.connect();
                byte[] buf = new byte[1024];
                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                while (true) {
                    int c = checkAck(in);
                    if (c != 'C') {
                        break;
                    }
                    // read '0644 '
                    in.read(buf, 0, 5);

                    long filesize = 0L;
                    while (true) {
                        if (in.read(buf, 0, 1) < 0) {
                            // error
                            break;
                        }
                        if (buf[0] == ' ') break;
                        filesize = filesize * 10L + (long) (buf[0] - '0');
                    }
                    String file = null;
                    for (int i = 0; ; i++) {
                        in.read(buf, i, 1);
                        if (buf[i] == (byte) 0x0a) {
                            file = new String(buf, 0, i);
                            break;
                        }
                    }
                    LOGGER.info("filesize=" + filesize + ", file=" + file);
                    // send '\0'
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();
                    // read a content of lfile
                    fos = new FileOutputStream(prefix == null ? localFile : prefix + file);
                    int foo;
                    while (true) {
                        if (buf.length < filesize) foo = buf.length;
                        else foo = (int) filesize;
                        foo = in.read(buf, 0, foo);
                        if (foo < 0) {
                            // error
                            break;
                        }
                        fos.write(buf, 0, foo);
                        filesize -= foo;
                        if (filesize == 0L) break;
                    }
                    fos.close();
                    fos = null;
                    if (checkAck(in) != 0) {
                        return;
                    }
                    // send '\0'
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();
                    LOGGER.info("file downloaded: " + file);
                }
                session.disconnect();
                return;
            } catch (Exception e) {
                LOGGER.error(e);
                try {
                    if (fos != null) fos.close();
                } catch (Exception ee) {
                }
            }
        }
    }

    private int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
