#!/bin/bash


r_file=$1
r_args=$2


    echo "Starting r script  $1"
    Rscript --vanilla $r_file $r_args
