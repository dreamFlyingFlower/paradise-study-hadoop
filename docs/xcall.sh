#!/bin/bash

params=$@
i=201
for (( i=201 ; i <= 204 ; i = $i + 1 )) ; do
    echo ============= s$i $params =============
    ssh s$i "$params"
done
