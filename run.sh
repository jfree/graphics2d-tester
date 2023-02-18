#!/bin/bash

for g2d in $(ls | grep g2d)
do
    echo "Running ${g2d}"
	cd "${g2d}"
	bash run.sh
	cd ..
done

