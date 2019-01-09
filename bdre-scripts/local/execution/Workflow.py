#!/usr/bin/env python
import urllib2
import json
import os
import sys
import commands
import time
import subprocess
BDRE_HOME = '~/bdre'
BDRE_APPS_HOME = '~/bdre_apps'
OOZIE_URL = 'http://localhost:11000/oozie'
output = commands.getstatusoutput('oozie job -run -config ' + BDRE_APPS_HOME + '/'+sys.argv[1]+'/'+sys.argv[2]+'/'+sys.argv[3]+'/job-' + sys.argv[3] + '.properties -oozie ' + OOZIE_URL)
print output
jobidstring = output[1]
print jobidstring
jobidparsed = jobidstring.split(":")
jobid = jobidparsed[1].strip()
print jobid
while True:
        req = urllib2.Request(OOZIE_URL +'/v1/jobs?jobtype=wf')
        response = urllib2.urlopen(req)
        output = response.read()
        j=json.loads(output)
        state = 'STATUS'
        for obj in j['workflows']:
                equalornot = (obj['id'] == jobid)
                if jobid == obj['id'] :
                        time.sleep(10)
                        state=obj['status']
                        break
        if state == 'SUCCEEDED':
                print "STATE IS SUCCEDED"
                exit(0)

        elif state == 'KILLED':
                print  'STATE IS KILLED'
                exit(1)

        elif state == 'FAILED':
                print  'STATE IS FAILED'
                exit(1)
        else:
                print 'STATE IS RUNNING'
                continue
