#!/usr/bin/env bash

date
time \
./scripts/_clean.sh && \
./scripts/_install-skip-tests.sh
date
say "done with clean install skip tests"
