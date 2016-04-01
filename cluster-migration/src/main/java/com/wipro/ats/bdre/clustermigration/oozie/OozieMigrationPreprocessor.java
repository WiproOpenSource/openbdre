package com.wipro.ats.bdre.clustermigration.oozie;

import com.wipro.ats.bdre.clustermigration.MigrationPreprocessor;
import com.wipro.ats.bdre.exception.BDREException;

/**
 * Created by cloudera on 3/31/16.
 */
public class OozieMigrationPreprocessor {

    private OozieMigrationPreprocessor(){
    }

    public static void main(String[] args) {
        try {
            MigrationPreprocessor migrationPreprocessor = new MigrationPreprocessor();
            migrationPreprocessor.execute(args);
        }
        catch(Exception e){
            throw new BDREException(e);
        }
    }
}
