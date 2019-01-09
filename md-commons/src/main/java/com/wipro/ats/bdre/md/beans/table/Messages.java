package com.wipro.ats.bdre.md.beans.table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by cloudera on 5/21/17.
 */
public class Messages {
    @NotNull
    @Size(max = 45)
    private String messagename;
    @NotNull
    @Size(max = 45)
    private String format;
    @NotNull
    @Size(max = 2048)
    private String messageSchema;

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @NotNull
    @Size(max = 45)
    private String connectionName;
    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    int counter;
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getMessagename() {
        return messagename;
    }

    public void setMessagename(String messagename) {
        this.messagename = messagename;
    }

    public String getMessageSchema() {
        return messageSchema;
    }

    public void setMessageSchema(String messageSchema) {
        this.messageSchema = messageSchema;
    }
}
