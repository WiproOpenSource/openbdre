#!/bin/bash

die () {
    echo >&2 "$@"
    exit 1
}

[ "$#" -eq 3 ] || die "3 arguments required, $# provided"
hdfs_file=$1
r_file=$2
hdfs_output=$3

if [[  ${hdfs_output} =~ ^\/tmp\/.*$ ]]; then
        echo "I will run the r script $2 on the hdfs $1"
        tmp_filename="/tmp/`date +"%Y%m%d.%H%M%S"`"
        echo "using tmp_dir $tmp_filename"
        tmp_output="/tmp/out`date +"%Y%m%d.%H%M%S"`"
        echo "using tmp_output $tmp_output"
        hadoop fs -get $hdfs_file $tmp_filename
        echo "r_file is $r_file"
        Rscript --vanilla $r_file $tmp_filename $tmp_output
        hadoop fs -rm -r $hdfs_output/
        hadoop fs -put $tmp_output $hdfs_output

else
  die "$hdfs_output must be in /tmp/"
fi